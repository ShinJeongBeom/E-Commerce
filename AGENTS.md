# AGENTS.md

## 프로젝트 개요

Ecommerce는 식물 및 식물 상품을 판매하는 커머스 서비스다.

## 현재 구현

- Backend: Java 17, Spring Boot, Gradle, Spring Security, JWT, Spring Data JPA
- Data: MySQL, Redis
- API/Test: JUnit 5, Spring Boot Test
- Frontend: Vite, React, TypeScript
- 현재 백엔드 도메인: `auth`, `member`, `product`, `cart`, `order`
- 현재 백엔드 API: `/auth/login`, `/auth/signup`, `/products`, `/cart`, `/orders`
- 현재 역할: `Role.USER`, `Role.ADMIN`
- 현재 상품 이미지는 `Product.imageUrl` 문자열로 관리한다.
- 현재 프론트엔드는 상품 목록을 mock 데이터로 렌더링하며, 로그인·회원가입 일부만 백엔드와 연결되어 있다.

## 향후 목표

- QueryDSL, Flyway 또는 Liquibase, Swagger/OpenAPI, GitHub Actions는 필요 시 별도 작업으로 도입한다.
- `Role.SELLER`, `SellerProfile`, 판매자 승인 흐름은 판매자 도메인 작업에서 추가한다.
- AWS S3 상품 이미지 업로드는 파일 업로드 도메인 작업에서 추가한다.
- `/api/v1` version prefix와 `ApiResponse.payload` 공통 응답은 API 계약 정리 작업에서 적용한다.
- FE-BE 연동은 현재 API와 DTO를 기준으로 먼저 맞추고, 공통 응답 전환은 별도 브랜치에서 진행한다.

## 작업 시작 체크리스트

- [ ] 현재 브랜치와 작업 목적을 확인한다.
- [ ] `git status -sb`로 기존 변경사항을 확인한다.
- [ ] BE 작업인지 FE 작업인지, 또는 두 저장소 연동 작업인지 구분한다.
- [ ] 이번 작업이 현재 구현을 다루는지 향후 목표를 다루는지 구분한다.
- [ ] 코드 변경이 허용된 작업인지 확인한다.
- [ ] 작업이 복잡하거나 여러 파일을 바꾸면 `PLANS.md` 형식으로 계획을 먼저 작성한다.
- [ ] 관련 문서(`api.md`, `FE_INTEGRATION.md`, `BRANCH_STRATEGY.md`, `commit.md`)를 확인한다.
- [ ] 반복 작업이면 `.codex/skills/<skill-name>/SKILL.md` 중 적용할 skill을 확인한다.
- [ ] 검증 기준이 애매하면 `evals/`의 평가 샘플과 PR 리뷰 체크리스트를 확인한다.
- [ ] 민감정보, 운영 DB, 외부 배포 작업이 포함되는지 확인한다.
- [ ] 파괴적 명령, 운영 데이터, 자격 증명, force push가 관련되면 `SAFETY.md`를 먼저 확인한다.

## AI 셋팅 브랜치 금지 규칙

`chore/ai-harness` 브랜치는 문서형 하네스만 추가하는 브랜치다. 이 브랜치에서는 기존 동작 영향이
없어야 한다.

- 코드 파일 수정 금지
- `build.gradle`, `application.properties`, `compose.yaml` 등 실행 설정 변경 금지
- `/api/v1` 도입 금지
- `ApiResponse.payload` 공통 응답 도입 금지
- QueryDSL, Flyway/Liquibase, Swagger/OpenAPI 의존성 추가 금지
- FE 연동 구현 금지
- 테스트 결과를 바꾸기 위한 테스트 삭제·약화 금지
- 문서 외 변경이 필요하면 별도 브랜치를 만든다.

## 개발 환경 셋업

- Java 버전 확인: `java -version`에서 Java 17 사용
- 빌드 도구는 저장소의 Gradle Wrapper(`./gradlew`)를 사용
- 의존성 및 빌드 확인: `./gradlew build`
- 로컬 인프라 설정 확인: `docker compose config`
- 로컬 MySQL·Redis 시작: `docker compose up -d mysql redis`
- 컨테이너 상태 확인: `docker compose ps`
- 로컬 profile은 `application-local.properties`를 기준으로 하고 `SPRING_PROFILES_ACTIVE=local`로 실행
- 로컬 영속 데이터는 MySQL을 사용하고 H2로 대체하지 않음
- 환경변수는 현재 `application.properties`와 gitignore된 로컬 파일 기준으로 설정
- 실제 `.env`, 비밀번호, JWT 키와 AWS 자격 증명은 커밋하지 않음
- Spring Boot 실행: `./gradlew bootRun --args='--spring.profiles.active=local'`

설정 파일과 README에 다른 공식 명령이 있으면 실제 저장소의 명령을 우선하고 이 문서를 갱신한다.

## 테스트

- Gradle 전체 테스트: `./gradlew test`
- Gradle 특정 테스트: `./gradlew test --tests '<TestClass 또는 패턴>'`
- Gradle 전체 검증: `./gradlew build`
- MySQL·Redis 통합 테스트는 현재 로컬 Docker Compose 또는 프로젝트 기존 테스트 방식을 우선 사용
- H2만으로 MySQL 고유 쿼리, 타입, 인덱스와 락 동작을 증명하지 않음
- 작업 완료 전 관련 단위 테스트, 통합 테스트와 전체 빌드를 통과시킬 것
- 프로젝트에 포맷터·정적 분석·커버리지 태스크가 있으면 함께 실행할 것

테스트를 삭제하거나 assertion을 약화해 실패를 숨기지 않는다. 환경 문제로 실행하지 못한 검증은
명령, 원인과 잔여 위험을 최종 보고에 기록한다.

## 빌드 & 배포

- 빌드: `./gradlew build`
- Docker Compose 검증: `docker compose config`
- Docker 이미지와 배포 절차는 실제 Dockerfile 및 GitHub Actions workflow가 생기면 그 설정을 따름
- 배포 대상: AWS EC2, Nginx, Docker 기반 환경
- 배포 전 DB migration의 애플리케이션 호환성과 복구 절차를 확인
- 배포 후 healthcheck와 핵심 API smoke test를 실행
- main 브랜치 push가 자동 배포를 실행하는지는 실제 GitHub Actions 설정을 확인하며 추정하지 않음
- 운영 배포, DB 권한 변경과 자격 증명 변경은 명시적 승인 없이 실행하지 않음

## 아키텍처 (코드만 봐서는 모를 수 있는 것들)

- Controller는 HTTP 요청·응답과 입력 검증만 담당
- Service는 유스케이스, 트랜잭션, 권한, 소유권과 상태 전이를 담당
- Repository는 JPA 영속성 로직을 담당하며 Controller에서 직접 호출하지 않음
- Entity를 API 응답으로 직접 반환하지 않고 요청·응답 DTO 사용
- MySQL을 원본 데이터 저장소로 유지하며 Redis는 캐시, 토큰, 멱등 키와 단기 상태에 사용
- 운영 스키마 변경은 Flyway 또는 Liquibase 도입 후 새 migration으로 작성
- 결제 콜백과 주문 생성은 멱등 처리하며 결제, 주문과 재고의 정합성을 검증
- 판매자 API를 추가할 때는 `Role.SELLER`와 `SellerProfile.APPROVED`를 모두 확인
- 관리자 변경 작업은 수행자, 대상, 시각과 사유를 감사 로그로 기록
- 외부 API와 S3 호출을 긴 DB 트랜잭션 안에서 수행하지 않고 실패 보상 또는 재처리 전략 사용

작업 전에 관련 상세 문서를 읽는다.

| 작업 | 상세 문서 |
|---|---|
| 작업 계획·범위·검증 순서 | `PLANS.md` |
| 역할·기능·상태 전이 | `PRODUCT_SPEC.md` |
| 계층·트랜잭션·오류·관측성 | `ARCHITECTURE.md` |
| REST API·DTO·Controller·응답 포맷 | `api.md` |
| FE-BE 연동 계약·순서 | `FE_INTEGRATION.md` |
| 브랜치 전략·작업 분리 | `BRANCH_STRATEGY.md` |
| Swagger·OpenAPI 문서화 | `API_OPENAPI.md` |
| MySQL·Redis·S3 | `DATA_REDIS_S3.md` |
| JWT·인가·관리자 기능 | `SECURITY.md` |
| 단위·통합·하네스 검증 | `TESTING.md` |
| Docker·DB 권한·EC2·CI/CD | `INFRA_CI.md` |
| 커밋·PR·Notion·포트폴리오 | `WORKFLOW.md` |
| 커밋 분리·메시지 작성·stage 검증 | `commit.md` |
| 출시·PR 전 최종 확인 | `CHECKLIST.md` |
| 파괴적 작업·비밀정보·운영 안전 | `SAFETY.md` |

## 반복 작업 Skill 라우팅

반복되는 작업은 `.codex/skills/`의 `SKILL.md`를 먼저 읽고 절차를 따른다. skill은 명령을
자동 실행하는 코드가 아니라, AI가 반복 업무를 일관되게 수행하기 위한 작업 메뉴얼이다.

| 상황 | Skill |
|---|---|
| API, DTO, Controller, 응답 포맷 변경 | `.codex/skills/api-change/SKILL.md` |
| Vite React FE와 Spring Boot BE 연동 | `.codex/skills/fe-be-integration/SKILL.md` |
| 커밋, push, PR, 배포 전 검증 | `.codex/skills/release-readiness/SKILL.md` |
| 도메인 완료 후 Notion·포트폴리오 정리 | `.codex/skills/docs-portfolio/SKILL.md` |

## 평가와 검증 레이어

- `evals/harness-eval-samples.json`은 작업 유형별 필수 통과 조건을 정의한다.
- `evals/pr-review-checklist.md`는 PR 전 자체 리뷰 질문과 결과 형식을 정의한다.
- eval 파일은 실제 테스트를 대체하지 않고, 테스트 전에 빠뜨리기 쉬운 품질 기준을 확인하는 용도다.
- 새 반복 작업이 생기면 skill을 추가하고, 해당 작업의 품질 기준을 eval에 추가한다.

## FE-BE 연동 규칙

- 현재 FE는 `/Users/shinjeongbeom/Java/SpringProject/E-Commerce-FE`의 Vite React 프로젝트다.
- 현재 BE는 `/Users/shinjeongbeom/SpringProject/E-Commerce`의 Spring Boot 프로젝트다.
- FE API base URL은 하드코딩보다 환경변수(`VITE_API_BASE_URL`)로 분리하는 것을 목표로 한다.
- 현재 BE `ProductResponse`는 FE `Product`와 필드가 다르므로 adapter를 둔다.
- adapter는 BE의 `id`, `name`, `plantType`, `careLevel`, `lightRequirement`, `wateringCycle`, `imageUrl`,
  `description`, `price`, `stock`, `status`를 FE 화면용 모델로 변환한다.
- 로그인 후 JWT는 `Authorization: Bearer <accessToken>` 헤더로 장바구니와 주문 API에 전달한다.
- BE 응답 포맷을 `ApiResponse.payload`로 바꾸는 작업은 FE 연동과 분리한다.

## 코딩 컨벤션

- 모든 새 백엔드 코드는 Java 17과 현재 프로젝트의 Spring Boot 버전에 맞게 작성
- 각 파일과 인접 패키지의 기존 코드 스타일, 이름과 구조를 우선
- 클래스·record·enum: `PascalCase`
- 메서드·변수·필드: `camelCase`
- 상수와 enum 값: `UPPER_SNAKE_CASE`
- 패키지명: 소문자
- 의존성 주입은 프로젝트 기존 방식과 생성자 주입을 우선
- 공개 API와 복잡한 도메인 규칙에만 필요한 설명을 남기고 자명한 주석은 추가하지 않음
- 금액 계산에 `float` 또는 `double`을 사용하지 않음
- 시간은 서버 기본 타임존에 암묵적으로 의존하지 않고 프로젝트 시간 정책 사용
- 예외 응답은 기존 전역 예외 처리와 오류 코드 형식을 따름

## 하지 말아야 할 것

- 새 프로덕션 의존성은 근거와 대안을 제시하고 사용자 또는 PR 논의 없이 추가하지 않음
- 아직 확정되지 않은 선택은 구현 완료로 표현하지 않고 `[ASSUMPTION]`으로 명시
- 사용자 변경이나 관련 없는 파일을 되돌리거나 커밋하지 않음
- 디버그 로그, `System.out.println`, 비밀정보와 개인정보를 프로덕션 코드에 남기지 않음
- `.env`, 운영 설정, JWT·AWS·DB 자격 증명을 커밋하지 않음
- 기존 migration 파일 수정, 운영 `ddl-auto` 스키마 변경과 무단 파괴적 migration 금지
- 운영 DB 권한·데이터, 외부 서비스와 자격 증명을 승인 없이 변경하지 않음
- 상품 이미지 바이너리를 MySQL에 저장하지 않음
- 테스트 실패 상태에서 완료로 보고하거나 자동 커밋하지 않음
- 명시적 요청 없는 force push, rebase, amend와 기존 커밋 수정 금지
- 측정하지 않은 성능, 사용자 수와 개선율을 포트폴리오에 작성하지 않음

## 작업 완료 전 체크리스트

- [ ] 요청 범위와 완료 조건을 충족함
- [ ] 관련 단위 테스트 통과
- [ ] MySQL·Redis 등 필요한 통합 테스트 통과
- [ ] 전체 테스트 및 빌드 통과
- [ ] 프로젝트의 정적 분석·포맷·커버리지 검사 통과
- [ ] API 문서가 실제 API 계약과 일치함
- [ ] DB migration, 인덱스와 하위 호환성 검토 완료
- [ ] Redis 키, TTL, 직렬화와 캐시 무효화 검토 완료
- [ ] JWT, 역할, 리소스 소유권과 판매자 승인 상태 검증 완료
- [ ] S3 업로드·삭제 실패와 보상 처리 검토 완료
- [ ] `git diff`에서 범위 이탈, 비밀정보와 디버그 코드가 없음
- [ ] 도메인 완료 시 Notion과 `docs/portfolio.md` 갱신
- [ ] 미실행 검증과 잔여 위험을 최종 보고에 기록
- [ ] 필수 검증 성공 후 논리적 작업 단위로 커밋

## PR 메시지 규칙

- 커밋 전 `commit.md`를 읽고 해당 형식과 분리 기준을 적용
- 커밋 메시지는 `<prefix> : #<issue-number> <수정 내용>` 형식을 사용
- 제목은 변경 결과를 명령형으로 간결하게 작성
- 본문에 변경 이유, 구현 내용과 영향 범위를 명시
- 실행한 테스트·빌드 명령과 결과를 기록
- DB, Redis, S3, API 계약과 배포 영향이 있으면 별도 표시
- 호환성을 깨는 변경과 migration 적용 순서를 명확히 작성
- 갱신한 Notion 페이지와 포트폴리오 문서를 연결
- 이번 PR에서 제외한 다음 작업, 기술 부채와 잔여 위험을 정리
- 다음 작업이 없으면 `없음`으로 명시
- push와 PR 생성은 현재 요청 범위에 포함된 경우에만 수행
