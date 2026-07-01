# Security Guide

## 인증

- 비밀번호는 Spring Security의 검증된 `PasswordEncoder`로 단방향 해시한다.
- Access Token은 짧은 만료 시간을 사용하고 Refresh Token은 Redis 또는 프로젝트 정책으로 관리한다.
- Refresh Token 재발급 시 회전과 이전 토큰 폐기를 검토한다.
- 로그아웃과 계정 정지 시 활성 Refresh Token을 폐기한다.
- JWT 서명 키, AWS 키, DB 비밀번호와 외부 API 키를 저장소에 커밋하지 않는다.
- 토큰을 URL query parameter에 전달하거나 로그에 남기지 않는다.

## 인가

- 현재 구현된 `Role.USER`, `Role.ADMIN`을 서버에서 검증한다.
- SELLER API를 추가하면 `Role.SELLER`와 `SellerProfile.APPROVED`를 함께 확인한다.
- 역할만으로 허용하지 않고 상품·주문·정산 등 리소스 소유권을 Service에서 검증한다.
- ADMIN 작업은 대상, 수행자, 시각, 결과와 사유를 감사 로그로 기록한다.
- 권한 부족과 리소스 미존재 응답이 타인의 데이터 존재 여부를 과도하게 노출하지 않게 한다.

## 입력과 데이터 보호

- 모든 시스템 경계에서 길이, 형식, 범위와 허용 값을 검증한다.
- 정렬 필드, 파일명, MIME type, URL과 enum 입력을 허용 목록으로 제한한다.
- 사용자 입력을 JPQL, SQL, 로그 템플릿, 파일 경로에 문자열로 직접 조합하지 않는다.
- 개인정보와 결제정보는 필요한 최소 범위만 수집·보관하고 응답과 로그에서 마스킹한다.
- CORS는 허용 origin·method·header를 명시하고 운영에서 wildcard credential 조합을 금지한다.
- 쿠키 인증을 사용하면 Secure, HttpOnly, SameSite와 CSRF 정책을 함께 설정한다.

## 운영 보안

- Swagger, Actuator와 관리 API는 운영 노출과 접근 제어를 명시한다.
- 에러 응답으로 내부 클래스명, 스택 트레이스, SQL과 인프라 주소를 노출하지 않는다.
- 의존성 보안 검사는 저장소의 Gradle 플러그인과 GitHub Actions 설정을 따른다.
- 인증·결제·관리자 기능 변경에는 정상, 미인증, 권한 부족, 다른 사용자 접근 테스트를 포함한다.
- 보안상 중요한 설정의 기본값은 실패 폐쇄(fail closed)로 둔다.

## 변경 시 체크리스트

- 공격자가 사용자 ID, seller ID 또는 product ID를 바꾸어 다른 데이터에 접근할 수 없는가?
- 정지·탈퇴·토큰 폐기 상태가 모든 보호 API에 즉시 반영되는가?
- 동시 요청이나 재시도로 결제·환불·권한 승인이 중복 처리되지 않는가?
- 로그·Swagger 예시·테스트 fixture에 실제 비밀정보가 없는가?
- 새 endpoint와 S3 객체가 필요한 최소 범위로만 공개되는가?
