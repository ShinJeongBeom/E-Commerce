package com.jeongbeom.ecommerce.payment.repository;

import com.jeongbeom.ecommerce.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByPaymentKey(String paymentKey);

    Optional<Payment> findByTossOrderId(String tossOrderId);

    boolean existsByPaymentKey(String paymentKey);
}
