package com.jeongbeom.ecommerce.product.entity.repository;

import com.jeongbeom.ecommerce.product.entity.CareLevel;
import com.jeongbeom.ecommerce.product.entity.LightRequirement;
import com.jeongbeom.ecommerce.product.entity.Product;
import com.jeongbeom.ecommerce.product.entity.ProductStatus;
import com.jeongbeom.ecommerce.product.entity.WateringCycle;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id = :id")
    Optional<Product> findByIdWithLock(@Param("id") Long id);

    // 단건 조회 메서드
    Optional<Product> findByIdAndStatusNot(Long id, ProductStatus status);

    @Query("""
        select p
        from Product p
        where (:careLevel is null or p.careLevel = :careLevel)
          and (:lightRequirement is null or p.lightRequirement = :lightRequirement)
          and (:wateringCycle is null or p.wateringCycle = :wateringCycle)
          and p.status <> com.jeongbeom.ecommerce.product.entity.ProductStatus.HIDDEN
        """)
    List<Product> findProductsByFilters(
            @Param("careLevel") CareLevel careLevel,
            @Param("lightRequirement") LightRequirement lightRequirement,
            @Param("wateringCycle") WateringCycle wateringCycle
    );

}
