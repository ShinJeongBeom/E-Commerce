package com.jeongbeom.ecommerce.admin.repository;

import com.jeongbeom.ecommerce.admin.entity.AdminBanner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminBannerRepository extends JpaRepository<AdminBanner, Long> {
    List<AdminBanner> findAllByOrderBySortOrderAscIdDesc();
}
