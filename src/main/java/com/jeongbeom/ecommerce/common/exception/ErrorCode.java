package com.jeongbeom.ecommerce.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원이 존재하지 않습니다."),
    MEMBER_NOT_ACTIVE(HttpStatus.FORBIDDEN, "이용할 수 없는 회원 상태입니다."),
    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "이미 사용 중인 이메일입니다."),
    DUPLICATE_LOGIN_ID(HttpStatus.BAD_REQUEST, "이미 사용 중인 아이디입니다."),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "상품이 존재하지 않습니다."),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "주문이 존재하지 않습니다."),
    INVALID_IN_PUT_VALUE(HttpStatus.BAD_REQUEST, "잘못된 요청입니다. "),
    NOT_ENOUGH_STOCK(HttpStatus.BAD_REQUEST, "재고가 부족합니다."),
    INVALID_STOCK_QUANTITY(HttpStatus.BAD_REQUEST, "재고 수량은 1개 이상이어야 합니다."),
    INVALID_CART_QUANTITY(HttpStatus.BAD_REQUEST, "장바구니 수량은 1개 이상이어야 합니다."),
    CART_NOT_FOUND(HttpStatus.NOT_FOUND, "장바구니가 존재하지 않습니다."),
    CART_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "장바구니 상품이 존재하지 않습니다."),
    SELLER_PROFILE_NOT_FOUND(HttpStatus.NOT_FOUND, "판매자 프로필이 존재하지 않습니다."),
    ORDER_ACCESS_DENIED(HttpStatus.FORBIDDEN,"본인의 주문만 처리할 수 있습니다." ),
    SELLER_ACCESS_DENIED(HttpStatus.FORBIDDEN, "판매자 권한이 필요합니다."),
    SELLER_NOT_APPROVED(HttpStatus.FORBIDDEN, "승인된 판매자만 사용할 수 있습니다."),
    PRODUCT_ACCESS_DENIED(HttpStatus.FORBIDDEN, "본인의 상품만 처리할 수 있습니다."),
    ORDER_ALREADY_CANCELLED(HttpStatus.BAD_REQUEST, "이미 취소된 주문입니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

}
