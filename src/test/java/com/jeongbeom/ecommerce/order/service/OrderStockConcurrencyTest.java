package com.jeongbeom.ecommerce.order.service;

import com.jeongbeom.ecommerce.common.entity.Role;
import com.jeongbeom.ecommerce.member.entity.Member;
import com.jeongbeom.ecommerce.member.repository.MemberRepository;
import com.jeongbeom.ecommerce.order.dto.OrderCreateRequestDto;
import com.jeongbeom.ecommerce.order.entity.repository.OrderRepository;
import com.jeongbeom.ecommerce.product.entity.CareLevel;
import com.jeongbeom.ecommerce.product.entity.LightRequirement;
import com.jeongbeom.ecommerce.product.entity.Product;
import com.jeongbeom.ecommerce.product.entity.ProductStatus;
import com.jeongbeom.ecommerce.product.entity.WateringCycle;
import com.jeongbeom.ecommerce.product.entity.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class OrderStockConcurrencyTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    @DisplayName("동시에 주문해도 재고가 음수가 되지 않는다")
    void 동시_주문_재고_차감_테스트() throws InterruptedException {
        Member member = memberRepository.save(
                new Member("concurrency" + System.currentTimeMillis() + "@test.com", "1234", "010-1111-2222", Role.USER)
        );

        Product product = productRepository.save(
                new Product(
                        "방울복랑금",
                        "다육식물",
                        CareLevel.NORMAL,
                        LightRequirement.MEDIUM,
                        WateringCycle.WEEKLY,
                        "https:111,111",
                        "화분 포함",
                        "부분 부분 금색 빛이 도는 식물",
                        5000,
                        10,
                        ProductStatus.ON_SALE
                )
        );

        int threadCount = 20;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    OrderCreateRequestDto requestDto = new OrderCreateRequestDto(
                            product.getId(),
                            1,
                            "신정범",
                            "010-1111-2222",
                            "서울시 강남구"
                    );

                    orderService.createOrder(member.getId(), requestDto);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        Product foundProduct = productRepository.findById(product.getId()).orElseThrow();

        assertThat(successCount.get()).isEqualTo(10);
        assertThat(failCount.get()).isEqualTo(10);
        assertThat(foundProduct.getStock()).isEqualTo(0);
        assertThat(orderRepository.findByMember(member)).hasSize(10);
    }
}
