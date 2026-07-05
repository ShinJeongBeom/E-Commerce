package com.jeongbeom.ecommerce.seller.repository;

import com.jeongbeom.ecommerce.member.entity.Member;
import com.jeongbeom.ecommerce.seller.entity.SellerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SellerProfileRepository extends JpaRepository<SellerProfile, Long> {
    Optional<SellerProfile> findByMember(Member member);
    boolean existsByMember(Member member);
}
