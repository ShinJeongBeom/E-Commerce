# API 작성 규칙

## 목표

Ecommerce API는 현재 구현된 계약을 깨지 않으면서 FE 연동을 진행한다. `/api/v1`, 공통 응답 포맷,
UseCase/command/result 구조는 향후 API 계약 정리 작업에서 적용한다.

## 현재 API

현재 백엔드는 다음 URL을 제공한다.

```text
POST   /auth/signup
POST   /auth/login
GET    /auth/check-login-id?loginId={loginId}

GET    /products
GET    /products/{productId}
POST   /products
PUT    /products/{productId}
DELETE /products/{productId}
POST   /products/images/presigned
POST   /products/images/complete

GET    /cart
POST   /cart
PATCH  /cart/items/{cartItemId}
DELETE /cart/{cartItemId}

POST   /orders
POST   /orders/{orderId}/cancel
GET    /orders/member/{memberId}
POST   /orders/cart
POST   /orders/cart/items

GET    /admin/dashboard
GET    /admin/orders
GET    /admin/members
PATCH  /admin/members/{memberId}/suspend
DELETE /admin/members/{memberId}
GET    /admin/products
PATCH  /admin/products/{productId}/hide
PATCH  /admin/products/{productId}/restore
DELETE /admin/products/{productId}
GET    /admin/sellers
PATCH  /admin/sellers/{sellerProfileId}/approve
PATCH  /admin/sellers/{sellerProfileId}/suspend
GET    /admin/banners
POST   /admin/banners
PUT    /admin/banners/{bannerId}
DELETE /admin/banners/{bannerId}
GET    /admin/policies
POST   /admin/policies
GET    /admin/boards
POST   /admin/boards
PUT    /admin/boards/{postId}
DELETE /admin/boards/{postId}
GET    /admin/inquiries
POST   /admin/inquiries
PATCH  /admin/inquiries/{inquiryId}/answer
GET    /admin/reports
POST   /admin/reports
PATCH  /admin/reports/{reportId}/resolve
GET    /admin/settlements
POST   /admin/settlements
PATCH  /admin/settlements/{settlementId}/complete
GET    /admin/audit-logs
```

- `/auth/**`는 인증 없이 접근한다.
- `GET /products/**`는 인증 없이 접근한다.
- 상품 등록·수정·삭제는 현재 `SELLER` 또는 `ADMIN` 권한이 필요하다.
- 장바구니와 주문 API는 JWT 인증이 필요하다.
- 현재 성공 응답은 `ApiResponse.payload`로 감싸지 않는다. Controller별 raw DTO, 문자열 또는 empty body를 반환한다.

## 현재 DTO 계약

### Auth

`POST /auth/signup`

```json
{
  "email": "user@example.com",
  "loginId": "user123",
  "role": "USER",
  "password": "password",
  "phone": "010-0000-0000"
}
```

- `role`: 공개 회원가입에서는 `USER` 또는 `SELLER`를 사용한다.
- `ADMIN`은 공개 회원가입으로 생성하지 않는다.

`POST /auth/login`

```json
{
  "loginId": "user123",
  "password": "password"
}
```

성공 응답:

```json
{
  "accessToken": "jwt-token",
  "loginId": "user123",
  "role": "USER"
}
```

`GET /auth/check-login-id?loginId=user123`

성공 응답:

```json
true
```

- `true`: 사용 가능한 아이디
- `false`: 이미 사용 중인 아이디

### Product

현재 `ProductResponse`:

```json
{
  "id": 1,
  "name": "방울복랑금",
  "plantType": "다육식물",
  "careLevel": "NORMAL",
  "lightRequirement": "MEDIUM",
  "wateringCycle": "WEEKLY",
  "imageUrl": "https://example.com/product.jpg",
  "potIncluded": "화분 포함",
  "description": "부분 부분 금색 빛이 도는 식물",
  "price": 5000,
  "stock": 10,
  "status": "ON_SALE"
}
```

### Product Image

상품 이미지는 백엔드 multipart 업로드가 아니라 Presigned URL을 이용해 S3로 직접 업로드한다.
두 API 모두 `SELLER` 또는 `ADMIN` 권한과 JWT가 필요하다.

`POST /products/images/presigned`

```json
{
  "contentType": "image/webp",
  "fileSize": 204800
}
```

성공 응답:

```json
{
  "uploadUrl": "https://bucket.s3.ap-northeast-2.amazonaws.com/products/7/uuid.webp?...",
  "objectKey": "products/7/uuid.webp",
  "expiresAt": "2026-09-03T00:10:00Z"
}
```

- 지원 타입: `image/jpeg`, `image/png`, `image/webp`
- 최대 크기: 5MB
- 발급된 URL의 유효시간: 10분
- 클라이언트는 발급 요청과 동일한 `Content-Type` 헤더로 S3에 `PUT`한다.

S3 업로드 성공 후 `POST /products/images/complete`를 호출한다.

```json
{
  "objectKey": "products/7/uuid.webp"
}
```

백엔드는 로그인 사용자의 경로인지 확인하고 S3 객체의 실제 크기와 콘텐츠 타입을 검증한다.
성공 응답:

```json
{
  "imageUrl": "https://cdn.example.com/products/7/uuid.webp",
  "objectKey": "products/7/uuid.webp"
}
```

`imageUrl`은 상품 등록·수정 요청에 사용한다. `cloud.aws.s3.public-base-url`이 설정되어 있으면
해당 CDN base URL을 사용하고, 설정되지 않으면 S3 객체 URL을 반환한다.

### Cart

`POST /cart`

```json
{
  "productId": 1,
  "quantity": 2
}
```

현재 `CartItemResponseDto`:

```json
{
  "cartItemId": 1,
  "productId": 1,
  "productName": "방울복랑금",
  "price": 5000,
  "quantity": 2
}
```

### Order

`POST /orders`

```json
{
  "productId": 1,
  "quantity": 2,
  "name": "신정범",
  "phone": "010-0000-0000",
  "address": "서울시 강남구"
}
```

`POST /orders/{orderId}/cancel`

- 본인의 `CREATED` 주문만 취소할 수 있다.
- 취소가 완료되면 주문 상태를 `CANCELLED`로 변경하고 주문 수량만큼 재고를 한 번 복구한다.
- 이미 취소된 주문은 기존 `ORDER_ALREADY_CANCELLED` 오류를 반환한다.
- 결제 완료 이후 상태는 결제 취소·환불 절차가 필요하므로 `INVALID_ORDER_STATUS_TRANSITION`과 `409 Conflict`를 반환한다.

### Admin Dashboard

`GET /admin/dashboard`

요청 헤더:

```text
Authorization: Bearer <ADMIN accessToken>
```

성공 응답:

```json
{
  "loginId": "admin",
  "today": "2026-07-09",
  "domainExpiresAt": "2026-12-31",
  "domainDday": 175,
  "quickMenus": [
    { "label": "정책관리", "target": "policy" },
    { "label": "통계", "target": "statistics" },
    { "label": "상품등록", "target": "products" }
  ],
  "todayStatus": {
    "memberSignupCount": 0,
    "memberWithdrawalCount": 0,
    "productCreatedCount": 0,
    "orderCount": 0
  },
  "pendingStatus": {
    "productReportCount": 0,
    "exchangeRefundCount": 0,
    "oneToOneInquiryCount": 0,
    "productInquiryCount": 0,
    "sellerApprovalCount": 0,
    "orderProcessingCount": 0
  },
  "marketplaceStatus": {
    "sellerApprovalWaitingCount": 0,
    "todaySellerSignupCount": 0,
    "settlementPendingAmount": 0,
    "reportedProductCount": 0,
    "reportedReviewCount": 0,
    "suspendedProductCount": 0
  },
  "improvementPosts": [
    {
      "title": "게시판 에디터 변경 요청",
      "authorLoginId": "admin02",
      "createdDate": "2026-07-01"
    }
  ],
  "manualPosts": [
    {
      "title": "상품 교환 처리 프로세스",
      "authorLoginId": "admin02",
      "createdDate": "2026-07-01"
    }
  ]
}
```

- `todayStatus.memberSignupCount`, `productCreatedCount`, `orderCount`는 당일 `createdAt` 기준으로 집계한다.
- `todayStatus.memberWithdrawalCount`는 탈퇴 상태 도메인이 생기기 전까지 기본값 `0`을 응답한다.
- `marketplaceStatus.sellerApprovalWaitingCount`는 `SellerProfile.PENDING` 기준으로 집계한다.
- `marketplaceStatus.todaySellerSignupCount`는 당일 생성된 `SellerProfile` 기준으로 집계한다.
- `marketplaceStatus.settlementPendingAmount`는 정산 도메인이 생기기 전까지 결제 이후 주문 금액 합계로 집계한다.
- `marketplaceStatus.suspendedProductCount`는 현재 판매 중지에 대응되는 `ProductStatus.HIDDEN` 기준으로 집계한다.
- 신고, 문의, 게시판, 정산 도메인은 관리자 운영용 최소 모델을 기준으로 집계하며 데이터가 없으면 `0` 또는 빈 목록을 응답한다.
- `/admin/**`는 `ADMIN` 권한이 필요하다.

### Admin Management

관리자 운영 탭은 `/admin/**` API를 사용한다.

- 회원 관리: 회원 목록 조회, 정지, 탈퇴 상태 처리
- 판매자 관리: 판매자 목록 조회, 승인, 정지
- 상품 관리: 전체 상품 목록 조회, 숨김, 복구, 삭제
- 배너 관리: 배너 등록, 수정, 삭제
- 기본 정책 관리: 정책 key 기준 저장
- 게시판 관리: 게시글 등록, 수정, 삭제
- 문의 관리: 문의 목록 조회와 답변 처리
- 신고 관리: 상품/리뷰 신고 등록과 처리 완료
- 정산 관리: 정산 대기 등록과 완료 처리
- 감사 로그: 관리자 작업 이력 조회

현재 회원 삭제는 물리 삭제가 아니라 `MemberStatus.DELETED` 상태 변경이다. 정지 또는 삭제된 회원은
로그인할 수 없다. 상품 삭제 API는 물리 삭제가 아니라 `ProductStatus.HIDDEN` 숨김 처리로 동작한다.

관리자 목록 API는 공통 query parameter를 지원한다.

```text
page=0
size=10
keyword=검색어
status=상태값
```

목록 응답은 다음 페이지 포맷을 사용한다.

```json
{
  "items": [],
  "page": 0,
  "size": 10,
  "totalElements": 0,
  "totalPages": 0
}
```

리소스별 추가 필터:

- `/admin/members`: `role`, `status`
- `/admin/orders`: `status`
- `/admin/products`: `status`
- `/admin/banners`: `visible`
- `/admin/boards`: `type`
- `/admin/inquiries`: `status`
- `/admin/reports`: `targetType`, `status`
- `/admin/settlements`: `status`

## FE 연동 규칙

현재 FE는 mock 상품 모델을 사용한다. BE 상품 응답과 필드가 다르므로 FE에서 adapter를 둔다.

```ts
type ProductResponse = {
  id: number
  name: string
  plantType: string
  careLevel: string
  lightRequirement: string
  wateringCycle: string
  imageUrl: string
  potIncluded: string
  description: string
  price: number
  stock: number
  status: string
}
```

FE 화면용 `Product`로 변환할 때는 다음 기준을 따른다.

- `id`, `name`, `price`는 그대로 사용한다.
- `imageUrl`은 FE `images[0]`로 변환한다.
- `description`은 FE `detail` 또는 `shortInfo`로 변환한다.
- `plantType`, `careLevel`, `lightRequirement`, `wateringCycle`, `potIncluded`는 FE `tags` 또는 상세 정보로 변환한다.
- `status === "HIDDEN"` 상품은 화면에 노출하지 않는다.
- BE에 없는 `salePrice`, `deliveryFee`, 복수 이미지, category는 FE 기본값 또는 별도 도메인 추가 전까지 adapter에서 처리한다.

인증이 필요한 API는 다음 헤더를 포함한다.

```text
Authorization: Bearer <accessToken>
```

## 향후 목표 API

API 계약 정리 작업에서 다음 구조로 이동한다.

```text
POST   /api/v1/auth/signup
POST   /api/v1/auth/login

GET    /api/v1/products
GET    /api/v1/products/{productId}
POST   /api/v1/admin/products
PATCH  /api/v1/admin/products/{productId}
DELETE /api/v1/admin/products/{productId}

GET    /api/v1/cart
POST   /api/v1/cart/items
PATCH  /api/v1/cart/items/{cartItemId}
DELETE /api/v1/cart/items/{cartItemId}

POST   /api/v1/orders
POST   /api/v1/orders/{orderId}/cancel
GET    /api/v1/me/orders
POST   /api/v1/orders/from-cart
POST   /api/v1/orders/from-cart-items
```

향후 목표 URL 규칙:

- API version은 `/api/v1` prefix로 관리한다.
- resource는 kebab-case와 복수형을 우선한다.
- 행위가 필요한 경우 마지막 segment에 action을 둔다.
- query parameter는 lower camelCase를 사용한다.

## 향후 목표 응답 포맷

공통 응답 포맷은 별도 API 계약 정리 작업에서 도입한다. 도입 전에는 FE 연동 코드가 현재 raw 응답과
목표 payload 응답을 혼동하지 않게 한다.

```json
{
  "payload": {
    "code": "PRODUCT_DETAIL_SUCCESS",
    "errorMessage": null,
    "response": {
      "id": 1,
      "name": "방울복랑금"
    }
  }
}
```

목표 포맷 필드:

- `code`: 애플리케이션 응답 코드
- `errorMessage`: 성공이면 `null`
- `response`: 실제 응답 객체

## Controller 작성 규칙

현재 구조에서는 기존 `controller -> service -> repository` 흐름을 유지한다.

Controller 역할:

- URL mapping
- HTTP request DTO validation
- 인증 사용자와 path/query/body 추출
- Service 호출
- HTTP response 반환

Controller에서 금지:

- 비즈니스 규칙 판단
- repository 직접 호출
- JPA Entity 직접 반환
- DB transaction 조정
- Redis 또는 S3 client 직접 호출

## 향후 DTO 구조

대규모 API 정리 작업을 할 때만 sealed interface, record, command/result 구조 도입을 검토한다.
현재 기능 연동 단계에서는 기존 DTO class를 우선 사용한다.

## 날짜와 시간

- API 날짜·시간 문자열은 ISO-8601 형식을 사용한다.
- 날짜만 필요하면 `LocalDate`를 사용한다.
- 절대 시점 또는 외부 연동에는 `OffsetDateTime` 또는 `Instant`를 검토한다.

예시:

```text
2026-05-03
2026-05-03T23:30:00
2026-05-03T23:30:00+09:00
```

## 검증 체크리스트

- [ ] 현재 API URL과 DTO가 FE 호출 코드와 일치함
- [ ] 로그인 응답의 `accessToken`을 FE가 저장하고 인증 API에 전달함
- [ ] BE `ProductResponse`에서 FE `Product`로 변환하는 adapter가 있음
- [ ] 장바구니와 주문 API에 Authorization 헤더가 포함됨
- [ ] 목표 API 규칙을 현재 구현으로 오해하지 않음
- [ ] 공통 응답 도입 시 FE 파싱 영향이 별도 작업으로 관리됨
