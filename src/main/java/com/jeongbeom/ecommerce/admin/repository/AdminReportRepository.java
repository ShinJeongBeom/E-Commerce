package com.jeongbeom.ecommerce.admin.repository;

import com.jeongbeom.ecommerce.admin.entity.AdminReport;
import com.jeongbeom.ecommerce.admin.entity.AdminReportStatus;
import com.jeongbeom.ecommerce.admin.entity.AdminReportTargetType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminReportRepository extends JpaRepository<AdminReport, Long> {
    List<AdminReport> findByTargetTypeOrderByCreatedAtDesc(AdminReportTargetType targetType);
    long countByTargetTypeAndStatus(AdminReportTargetType targetType, AdminReportStatus status);
}
