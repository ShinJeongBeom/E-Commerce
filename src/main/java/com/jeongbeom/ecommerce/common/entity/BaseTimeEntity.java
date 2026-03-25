package com.jeongbeom.ecommerce.common.entity;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass //테이블 생성 제어 , 필드만 상속하기 위해
@Getter
@EntityListeners(AuditingEntityListener.class) //엔티티 변경 이벤트를 감지해서 자동으로 값을 넣어줌
//공통 컬럼을 재사용 하기 위한 추상 클래스
public abstract class BaseTimeEntity {

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;


}
