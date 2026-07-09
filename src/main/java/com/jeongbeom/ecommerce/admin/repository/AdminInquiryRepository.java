package com.jeongbeom.ecommerce.admin.repository;

import com.jeongbeom.ecommerce.admin.entity.AdminInquiry;
import com.jeongbeom.ecommerce.admin.entity.AdminInquiryStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminInquiryRepository extends JpaRepository<AdminInquiry, Long> {
    long countByStatus(AdminInquiryStatus status);
}
