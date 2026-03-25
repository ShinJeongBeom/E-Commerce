package com.jeongbeom.ecommerce.member;

import com.jeongbeom.ecommerce.common.entity.Role;
import com.jeongbeom.ecommerce.member.entity.Member;
import com.jeongbeom.ecommerce.member.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional //db에 안남김
public class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    void 회원_저장_테스트(){
        Member member = new Member(
                "test@123457",
                "1234" ,
                "010-1234-1234" ,
                Role.USER
        );

        memberRepository.save(member); //테스트 저장
    }
}
