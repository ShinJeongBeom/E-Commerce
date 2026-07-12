package com.jeongbeom.ecommerce.payment.service;

import com.jeongbeom.ecommerce.common.exception.CustomException;
import com.jeongbeom.ecommerce.common.exception.ErrorCode;
import com.jeongbeom.ecommerce.order.entity.Order;
import com.jeongbeom.ecommerce.order.entity.OrderStatus;
import com.jeongbeom.ecommerce.order.entity.repository.OrderRepository;
import com.jeongbeom.ecommerce.order.exception.OrderAccessDeniedException;
import com.jeongbeom.ecommerce.order.exception.OrderNotFoundException;
import com.jeongbeom.ecommerce.payment.dto.PaymentConfirmRequest;
import com.jeongbeom.ecommerce.payment.dto.PaymentResponse;
import com.jeongbeom.ecommerce.payment.dto.TossPaymentConfirmResponse;
import com.jeongbeom.ecommerce.payment.dto.TossPaymentErrorResponse;
import com.jeongbeom.ecommerce.payment.entity.Payment;
import com.jeongbeom.ecommerce.payment.entity.PaymentStatus;
import com.jeongbeom.ecommerce.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private static final URI TOSS_PAYMENT_CONFIRM_URI = URI.create("https://api.tosspayments.com/v1/payments/confirm");

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Value("${toss.secret-key:}")
    private String tossSecretKey;

    @Transactional
    public PaymentResponse confirmPayment(Long memberId, PaymentConfirmRequest request) {
        Order order = orderRepository.findByOrderNumber(request.getOrderId())
                .orElseThrow(OrderNotFoundException::new);

        if (!order.getMember().getId().equals(memberId)) {
            throw new OrderAccessDeniedException();
        }

        if (order.getTotalPrice() != request.getAmount()) {
            throw new CustomException(ErrorCode.PAYMENT_AMOUNT_MISMATCH);
        }

        return paymentRepository.findByTossOrderId(request.getOrderId())
                .map(PaymentResponse::new)
                .orElseGet(() -> confirmNewPayment(order, request));
    }

    private PaymentResponse confirmNewPayment(Order order, PaymentConfirmRequest request) {
        if (paymentRepository.existsByPaymentKey(request.getPaymentKey())) {
            throw new CustomException(ErrorCode.PAYMENT_ALREADY_CONFIRMED);
        }

        TossPaymentConfirmResponse tossResponse = requestTossPaymentConfirm(request);
        Payment payment = new Payment(
                order,
                tossResponse.paymentKey(),
                tossResponse.orderId(),
                tossResponse.orderName(),
                tossResponse.totalAmount(),
                tossResponse.method(),
                PaymentStatus.valueOf(tossResponse.status()),
                tossResponse.requestedAt(),
                tossResponse.approvedAt(),
                tossResponse.receipt() == null ? null : tossResponse.receipt().url()
        );

        Payment savedPayment = paymentRepository.save(payment);

        if (savedPayment.getStatus() == PaymentStatus.DONE) {
            order.changeStatus(OrderStatus.PAID);
        }

        return new PaymentResponse(savedPayment);
    }

    private TossPaymentConfirmResponse requestTossPaymentConfirm(PaymentConfirmRequest request) {
        if (tossSecretKey == null || tossSecretKey.isBlank()) {
            throw new CustomException(ErrorCode.PAYMENT_SECRET_KEY_NOT_FOUND);
        }

        try {
            String requestBody = objectMapper.writeValueAsString(Map.of(
                    "paymentKey", request.getPaymentKey(),
                    "orderId", request.getOrderId(),
                    "amount", request.getAmount()
            ));

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(TOSS_PAYMENT_CONFIRM_URI)
                    .header("Authorization", createAuthorizationHeader())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throwTossPaymentException(response.body());
            }

            return objectMapper.readValue(response.body(), TossPaymentConfirmResponse.class);
        } catch (JacksonException e) {
            throw new CustomException(ErrorCode.PAYMENT_CONFIRM_FAILED);
        } catch (IOException e) {
            throw new CustomException(ErrorCode.PAYMENT_CONFIRM_FAILED);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CustomException(ErrorCode.PAYMENT_CONFIRM_FAILED);
        }
    }

    private String createAuthorizationHeader() {
        String credential = tossSecretKey + ":";
        return "Basic " + Base64.getEncoder().encodeToString(credential.getBytes(StandardCharsets.UTF_8));
    }

    private void throwTossPaymentException(String responseBody) {
        try {
            objectMapper.readValue(responseBody, TossPaymentErrorResponse.class);
            throw new CustomException(ErrorCode.PAYMENT_CONFIRM_FAILED);
        } catch (JacksonException e) {
            throw new CustomException(ErrorCode.PAYMENT_CONFIRM_FAILED);
        }
    }
}
