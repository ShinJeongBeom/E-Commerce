package com.jeongbeom.ecommerce.member.repository;

import com.jeongbeom.ecommerce.member.entity.Member;
import com.jeongbeom.ecommerce.common.entity.Role;
import com.jeongbeom.ecommerce.member.entity.MemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    Optional<Member> findByLoginId(String loginId);
    boolean existsByLoginId(String loginId);
    boolean existsByEmail(String email);
    long countByRole(Role role);
    long countByCreatedAtBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
    long countByStatusAndUpdatedAtBetween(MemberStatus status, LocalDateTime startDateTime, LocalDateTime endDateTime);
}
