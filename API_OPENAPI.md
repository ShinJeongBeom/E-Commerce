# REST API and OpenAPI Guide

API 구조, DTO, Controller와 공통 응답 포맷은 `api.md`를 우선 적용한다.
이 문서는 REST 계약의 호환성과 Swagger/OpenAPI 문서화 규칙을 보완한다.

## REST 규칙

- 현재 API는 `/auth`, `/products`, `/cart`, `/orders`를 사용한다.
- 신규 API 계약 정리 작업에서는 `/api/v1`과 복수형 리소스 명사를 사용한다.
- `GET` 조회, `POST` 생성, `PUT` 전체 교체, `PATCH` 부분 변경, `DELETE` 삭제 의미를 지킨다.
- Entity를 반환하지 않고 요청·응답 DTO를 사용한다.
- 상태 코드는 실제 결과에 맞추고 모든 응답을 `200 OK`로 감싸지 않는다.
- 목록은 페이지네이션하고 최대 크기를 제한한다. 정렬·필터 필드는 허용 목록으로 검증한다.
- 생성 응답에는 식별자를 반환하고 프로젝트 규칙이 있으면 `Location`을 제공한다.
- 변경 API는 중복 요청과 재시도에 대한 멱등성을 검토한다.

## 리소스 기준

- 인증·회원: `/auth`, `/users`, `/users/me`
- 판매자: `/seller-applications`, `/sellers`, `/seller/products`, `/seller/orders`, `/seller/settlements` (향후 목표)
- 상품·카테고리: `/products`, `/categories`
- 장바구니·찜: `/cart/items`, `/bookmarks`
- 주문·결제·배송: `/orders`, `/payments`, `/shipments`
- 리뷰: `/products/{productId}/reviews`
- 관리자: `/admin/users`, `/admin/sellers`, `/admin/products`, `/admin/orders`
- 사이트 관리: `/admin/notices`, `/admin/events`, `/admin/banners`

현재 상품 목록은 `careLevel`, `lightRequirement`, `wateringCycle` 필터 중심이다.
검색 조건이 복잡해지면 Spring Data JPA Specification 또는 QueryDSL 도입을 검토한다.

## 계약과 호환성

- Bean Validation 제약과 도메인 검증을 함께 사용한다.
- API 계약 변경 시 클라이언트 호환성, 인증·인가, 오류 응답과 테스트를 갱신한다.
- 필드 제거·타입 변경·의미 변경은 파괴적 변경으로 취급한다.
- 날짜, 금액, enum, null 가능 여부와 페이지 응답 형식을 일관되게 유지한다.
- 프론트엔드 Axios 호출에서 처리할 수 있도록 오류 코드와 사용자 메시지를 분리한다.

## Swagger/OpenAPI

- 현재 Swagger/OpenAPI는 아직 필수 구성으로 고정하지 않는다.
- Swagger를 도입할 때는 Spring Boot 버전과 호환되는 라이브러리 한 계열로 통일한다.
- 각 API에 태그, 요약, 주요 응답, 인증 요구와 필요한 예시를 작성한다.
- DTO의 의미가 불명확한 필드에는 설명·예시·제약을 기록한다.
- 공통 오류, 페이지 응답과 JWT 보안 스키마는 중앙 설정으로 재사용한다.
- 문서 상태 코드와 실제 Controller·예외 처리 동작을 일치시킨다.
- Swagger UI와 `/v3/api-docs` 노출은 프로필별로 제어하고 운영 공개는 보안 정책을 따른다.

API 변경 후 애플리케이션 컨텍스트와 계약 테스트를 실행한다. 로컬 실행이 가능하면
`/v3/api-docs`가 정상 JSON을 반환하고 변경 경로와 스키마를 포함하는지 확인한다.
