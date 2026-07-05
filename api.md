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

GET    /cart
POST   /cart
PATCH  /cart/items/{cartItemId}
DELETE /cart/{cartItemId}

POST   /orders
POST   /orders/{orderId}/cancel
GET    /orders/member/{memberId}
POST   /orders/cart
POST   /orders/cart/items
```

- `/auth/**`는 인증 없이 접근한다.
- `GET /products/**`는 인증 없이 접근한다.
- 상품 등록·수정·삭제는 현재 `ADMIN` 권한이 필요하다.
- 장바구니와 주문 API는 JWT 인증이 필요하다.
- 현재 성공 응답은 `ApiResponse.payload`로 감싸지 않는다. Controller별 raw DTO, 문자열 또는 empty body를 반환한다.

## 현재 DTO 계약

### Auth

`POST /auth/signup`

```json
{
  "email": "user@example.com",
  "loginId": "user123",
  "password": "password",
  "phone": "010-0000-0000"
}
```

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
  "accessToken": "jwt-token"
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
