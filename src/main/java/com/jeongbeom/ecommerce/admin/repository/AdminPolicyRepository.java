package com.jeongbeom.ecommerce.admin.repository;

import com.jeongbeom.ecommerce.admin.entity.AdminPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminPolicyRepository extends JpaRepository<AdminPolicy, Long> {
    Optional<AdminPolicy> findByPolicyKey(String policyKey);
}
