package com.jeongbeom.ecommerce.admin.repository;

import com.jeongbeom.ecommerce.admin.entity.AdminAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminAuditLogRepository extends JpaRepository<AdminAuditLog, Long> {
}
