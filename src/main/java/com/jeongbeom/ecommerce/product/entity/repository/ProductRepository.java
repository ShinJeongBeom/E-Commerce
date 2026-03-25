package com.jeongbeom.ecommerce.product.entity.repository;

import com.jeongbeom.ecommerce.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
