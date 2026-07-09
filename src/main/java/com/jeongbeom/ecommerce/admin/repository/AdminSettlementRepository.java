package com.jeongbeom.ecommerce.admin.repository;

import com.jeongbeom.ecommerce.admin.entity.AdminSettlement;
import com.jeongbeom.ecommerce.admin.entity.AdminSettlementStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AdminSettlementRepository extends JpaRepository<AdminSettlement, Long> {
    @Query("select coalesce(sum(s.amount), 0) from AdminSettlement s where s.status = :status")
    long sumAmountByStatus(@Param("status") AdminSettlementStatus status);
}
