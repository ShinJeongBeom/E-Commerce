# MySQL, Redis and S3 Guide

## 목표 데이터 모델

- `users`: id, email(unique), password_hash, name, phone, role, status, timestamps
- `seller_profiles`: id, user_id(unique FK), business fields, status, review fields, timestamps
- `categories`: id, name, parent_id(self FK), timestamps
- `products`: id, seller_id(FK), category_id(FK), name, description, price, stock, status, timestamps
- `product_images`: id, product_id(FK), image_url, object_key, sort_order, is_thumbnail, created_at
- `carts`: id, user_id(unique FK), timestamps
- `cart_items`: id, cart_id(FK), product_id(FK), quantity, timestamps
- `orders`: id, user_id(FK), order_number(unique), total_price, status, receiver fields, address, timestamps
- `order_items`: id, order_id(FK), product_id(FK), seller_id(FK), product_name_snapshot, unit_price, quantity, status, timestamps
- 추가 테이블: `payments`, `shipments`, `reviews`, `bookmarks`, `settlements`, `notices`, `events`, `banners`, `admin_audit_logs`

초기 ERD의 `order_items.user_id/category_id`, `cart_items.user_id/category_id`,
`product_images.category_id`는 관계로 확인 가능한 중복 FK이므로 저장하지 않는다.

## MySQL 규칙

- local profile의 영속 데이터베이스도 MySQL을 사용한다. 개발 편의를 이유로 H2로 바꾸지 않는다.
- Flyway 또는 Liquibase 도입 후에는 새 마이그레이션으로 변경하고 적용된 파일을 수정하지 않는다.
- 운영에서 Hibernate `ddl-auto`로 스키마를 생성·변경하지 않는다.
- null, 기본값, 길이, FK, unique와 인덱스를 명시한다.
- 주문 항목에 상품명과 가격 스냅샷을 저장해 상품 변경 후에도 주문 이력을 보존한다.
- `(cart_id, product_id)`, `(user_id, product_id)` 찜과 리뷰 정책에 필요한 unique를 둔다.
- 삭제는 참조 관계와 이력 보존 요구를 검토해 soft/hard delete를 선택한다.
- 마이그레이션은 가능하면 expand-migrate-contract 순서로 하위 호환되게 설계한다.
- MySQL 고유 쿼리와 락은 실제 MySQL 통합 테스트로 검증한다.

## Local profile 기준

실제 환경변수 이름과 migration 도구는 저장소 설정을 우선한다. 기본 구조는 다음과 같다.

```yaml
spring:
  datasource:
    url: ${LOCAL_DB_URL}
    username: ${LOCAL_DB_USERNAME}
    password: ${LOCAL_DB_PASSWORD}
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    open-in-view: false
    hibernate:
      ddl-auto: validate
  data:
    redis:
      host: ${LOCAL_REDIS_HOST:localhost}
      port: ${LOCAL_REDIS_PORT:6379}
```

- `LOCAL_DB_URL`은 Docker Compose의 MySQL schema를 가리키는 JDBC URL로 설정한다.
- schema와 connection은 `utf8mb4`를 사용하고 timezone은 프로젝트 시간 정책과 일치시킨다.
- migration 도구를 활성화해 애플리케이션 시작 전에 schema 버전을 확인한다.
- `open-in-view=false`를 기준으로 Service transaction 안에서 필요한 연관 데이터를 조회한다.
- 비밀번호가 포함된 로컬 설정 파일은 gitignore하고 `.env.example`에는 변수 이름만 기록한다.
- 애플리케이션 시작 시 MySQL 연결, migration, Redis 연결과 health endpoint를 확인한다.

## Redis 규칙

사용 범위:

- Refresh Token 또는 세션과 로그아웃 차단 목록
- 상품·카테고리 캐시
- 주문·결제 멱등 키
- 근거가 있는 경우의 분산 락 또는 단기 상태

키 예시:

```text
ecommerce:auth:refresh:<userId>
ecommerce:product:<productId>
ecommerce:idempotency:order:<key>
```

- 모든 단기·캐시 키에 TTL을 지정한다.
- 키 형식, TTL과 직렬화 포맷 변경의 호환성을 검토한다.
- DB 변경 후 관련 캐시를 커밋 시점에 무효화한다.
- Redis 장애와 캐시 미스에도 MySQL 원본의 정합성을 유지한다.
- 분산 락은 소유권 확인, 만료, 예외 해제와 재시도 정책을 테스트한다.

## AWS S3 이미지

- 이미지 바이너리는 DB에 저장하지 않고 S3의 `object_key`와 `image_url`만 저장한다.
- MIME type, 확장자, 크기와 이미지 개수를 서버에서 검증한다.
- 객체 키는 서버에서 생성하고 원본 파일명과 경로 입력을 신뢰하지 않는다.
- 공개 버킷을 기본으로 사용하지 않고 정책에 따라 CloudFront 또는 서명 URL을 사용한다.
- 상품 생성 실패 시 업로드 객체를 보상 삭제한다.
- 이미지 교체·삭제 실패는 추적하고 재처리할 수 있게 한다.
- 상품당 대표 이미지는 하나만 허용하며 정렬 순서 정책을 둔다.
- AWS 자격 증명과 버킷 정보는 환경변수나 배포 비밀 저장소에서 주입한다.
