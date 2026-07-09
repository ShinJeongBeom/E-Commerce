# FE-BE Integration Guide

## 목적

Ecommerce FE와 BE를 연결할 때 현재 API 계약을 기준으로 안전하게 연동한다. 이 문서는 FE mock 데이터를
BE API로 교체하는 순서와 주의점을 정의한다.

## 저장소 위치

- Backend: `/Users/shinjeongbeom/SpringProject/E-Commerce`
- Frontend: `/Users/shinjeongbeom/Java/SpringProject/E-Commerce-FE`

## 현재 상태

### Backend

- Spring Boot, Java 17, Spring Security, JWT, Spring Data JPA
- 현재 API는 `/auth`, `/products`, `/cart`, `/orders`를 사용한다.
- 현재 응답은 `ApiResponse.payload`로 감싸지 않은 raw DTO, 문자열 또는 empty body다.
- `GET /products/**`는 공개 API다.
- `/cart`, `/orders`는 JWT 인증이 필요하다.

### Frontend

- Vite React TypeScript 프로젝트다.
- 현재 상품은 `App.tsx` 내부 mock 배열로 렌더링한다.
- 로그인과 회원가입은 일부 백엔드 호출이 연결되어 있다.
- API base URL은 현재 `http://localhost:8080` 하드코딩 상태다.

## 연동 원칙

- 먼저 현재 BE API 계약에 맞춘다.
- `/api/v1` 전환과 `ApiResponse.payload` 전환은 FE 연동 작업과 분리한다.
- FE에서 API 호출 모듈을 분리하고 화면 컴포넌트에 fetch 로직을 계속 쌓지 않는다.
- JWT는 로그인 성공 후 저장하고 인증 API 요청에 `Authorization: Bearer <accessToken>`으로 전달한다.
- BE DTO와 FE 화면 모델이 다르면 adapter를 둔다.

## API Base URL

목표:

```ts
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080'
```

FE `.env.local` 예시:

```text
VITE_API_BASE_URL=http://localhost:8080
```

실제 `.env.local`은 커밋하지 않는다.

## Auth 연동

### 회원가입

현재 BE `SignupRequestDto`:

```json
{
  "email": "user@example.com",
  "loginId": "user123",
  "role": "USER",
  "password": "password",
  "phone": "010-0000-0000"
}
```

FE는 회원가입 전에 `GET /auth/check-login-id?loginId={loginId}`로 아이디 중복 여부를 확인한다.
응답이 `true`이면 사용 가능, `false`이면 이미 사용 중인 아이디다.
회원가입 계정 유형은 구매자 `USER`, 판매자 `SELLER` 중 하나를 보낸다. `ADMIN`은 공개 회원가입으로
만들지 않는다.

현재 FE 회원가입 폼에는 `name`, `address`, `addressDetail`도 있다. BE가 아직 받지 않는 값은
회원가입 요청 body에 포함하지 않거나, BE 회원 도메인 확장 작업에서 DTO와 Entity를 먼저 확장한다.

### 로그인

현재 BE `LoginRequestDto`:

```json
{
  "loginId": "user123",
  "password": "password"
}
```

현재 BE `LoginResponseDto`:

```json
{
  "accessToken": "jwt-token",
  "loginId": "user123",
  "role": "USER"
}
```

FE는 로그인 응답의 `role`이 `USER`이면 기존 쇼핑몰 화면을 보여주고, `SELLER` 또는 `ADMIN`이면
판매자/관리자 대시보드 화면으로 이동한다.

FE는 `accessToken`을 저장하고, 인증 요청에서 다음 헤더를 사용한다.

```text
Authorization: Bearer <accessToken>
```

## Admin Dashboard 연동

관리자 로그인 후 FE는 `role === "ADMIN"`이면 쇼핑몰 화면 대신 관리자 메인 화면으로 이동한다.

```text
GET /admin/dashboard
Authorization: Bearer <accessToken>
```

현재 응답은 raw DTO다.

```ts
type AdminDashboardResponse = {
  loginId: string
  today: string
  domainExpiresAt: string
  domainDday: number
  quickMenus: Array<{ label: string; target: string }>
  todayStatus: {
    memberSignupCount: number
    memberWithdrawalCount: number
    productCreatedCount: number
    orderCount: number
  }
  pendingStatus: {
    productReportCount: number
    exchangeRefundCount: number
    oneToOneInquiryCount: number
    productInquiryCount: number
    sellerApprovalCount: number
    orderProcessingCount: number
  }
  marketplaceStatus: {
    sellerApprovalWaitingCount: number
    todaySellerSignupCount: number
    settlementPendingAmount: number
    reportedProductCount: number
    reportedReviewCount: number
    suspendedProductCount: number
  }
  improvementPosts: Array<{ title: string; authorLoginId: string; createdDate: string }>
  manualPosts: Array<{ title: string; authorLoginId: string; createdDate: string }>
}
```

주의:

- 관리자 메인 화면은 기존 쇼핑몰 레이아웃과 분리된 운영 콘솔로 렌더링한다.
- Today 현황은 회원가입, 회원탈퇴, 상품등록, 주문건만 표시한다.
- 판매자 승인 대기, 오늘 신규 판매자 가입, 정산 대기 금액, 신고된 상품, 신고된 리뷰,
  판매 중지 상품은 오픈마켓 운영 지표로 별도 표시한다.
- 신고, 문의, 게시판 도메인은 아직 없으므로 관련 영역은 BE 기본값을 화면에 표시한다.
- 관리자 API는 `ADMIN` 권한이 필요하며, 권한 실패 시 FE 에러 상태를 표시한다.

## Product 연동

현재 BE `ProductResponse`:

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

현재 FE `Product` 모델은 `category`, `salePrice`, `deliveryFee`, `shortInfo`, `detail`, `tags`,
`images`를 사용한다. BE 응답을 그대로 화면에 넣지 말고 adapter를 둔다.

adapter 예시:

```ts
function toProduct(response: ProductResponse): Product {
  return {
    id: response.id,
    name: response.name,
    category: 'echeveria',
    price: response.price,
    deliveryFee: 3000,
    shortInfo: response.description,
    detail: response.description,
    tags: [
      response.plantType,
      response.careLevel,
      response.lightRequirement,
      response.wateringCycle,
      response.potIncluded,
    ].filter(Boolean),
    images: [response.imageUrl],
  }
}
```

주의:

- `status === "HIDDEN"` 상품은 화면에 노출하지 않는다.
- BE에 아직 category, salePrice, deliveryFee, 복수 이미지가 없으므로 FE 기본값을 사용한다.
- 이 기본값은 임시이며 상품 도메인 확장 작업에서 정리한다.

## Cart 연동

장바구니 추가:

```text
POST /cart
Authorization: Bearer <accessToken>
Content-Type: application/json
```

```json
{
  "productId": 1,
  "quantity": 2
}
```

장바구니 조회:

```text
GET /cart
Authorization: Bearer <accessToken>
```

현재 응답:

```ts
type CartItemResponseDto = {
  cartItemId: number
  productId: number
  productName: string
  price: number
  quantity: number
}
```

## Order 연동

단일 상품 주문:

```text
POST /orders
Authorization: Bearer <accessToken>
Content-Type: application/json
```

```json
{
  "productId": 1,
  "quantity": 2,
  "name": "신정범",
  "phone": "010-0000-0000",
  "address": "서울시 강남구"
}
```

## 권장 작업 순서

1. FE에 `VITE_API_BASE_URL`과 API client를 추가한다.
2. 로그인·회원가입 요청 body를 현재 BE DTO에 맞춘다.
3. 상품 목록 mock을 `GET /products`로 교체한다.
4. `ProductResponse -> Product` adapter를 추가한다.
5. 상품 상세 선택 로직을 API 데이터 기준으로 정리한다.
6. JWT 헤더를 포함해 장바구니 추가·조회·수량 변경·삭제를 연결한다.
7. 주문 생성 API를 연결한다.
8. 이후 `/api/v1`과 공통 응답 전환은 별도 브랜치에서 진행한다.

## 연동 체크리스트

- [ ] FE API base URL이 환경변수로 분리됨
- [ ] 로그인 성공 시 `accessToken` 저장
- [ ] 인증 API에 `Authorization` 헤더 전달
- [ ] 회원가입 요청 body가 현재 BE `SignupRequestDto`와 일치
- [ ] 회원가입 계정 유형을 `USER` 또는 `SELLER`로 전달
- [ ] 회원가입 전 `GET /auth/check-login-id`로 아이디 중복확인 수행
- [ ] 로그인 응답 `role`에 따라 사용자 화면과 판매자 대시보드를 분기
- [ ] 상품 mock 제거 또는 API 데이터 우선 사용
- [ ] `ProductResponse -> Product` adapter 존재
- [ ] 장바구니 API가 BE DTO와 일치
- [ ] 주문 API가 BE DTO와 일치
- [ ] BE 응답 포맷 변경 작업과 FE 연동 작업이 분리됨
