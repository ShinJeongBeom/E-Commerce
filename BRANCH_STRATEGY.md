# Branch Strategy

## 목적

AI 셋팅, FE-BE 연동, API 계약 정리, 인프라 도입 작업을 섞지 않는다. 각 브랜치는 하나의 목적만
가진다.

## 브랜치 규칙

- 브랜치 이름은 작업 성격과 목적이 보이게 작성한다.
- 문서, 기능, 리팩터링, 인프라 작업을 가능하면 분리한다.
- 코드 동작을 바꾸는 작업과 문서형 하네스 작업을 섞지 않는다.
- 작업 전 `git status -sb`로 현재 변경사항을 확인한다.
- 작업 중 다른 사람이 만든 변경을 되돌리지 않는다.

## 권장 브랜치

| 브랜치 | 목적 | 허용 변경 |
|---|---|---|
| `chore/ai-harness` | AI 작업 규칙과 문서형 하네스 추가 | `.md` 문서만 |
| `feat/fe-api-integration` | 현재 BE API와 FE 연결 | FE API client, adapter, 화면 연동 |
| `refactor/api-response-format` | 공통 응답 포맷 도입 | BE `ApiResponse`, Controller 응답, FE parser |
| `refactor/api-v1-routing` | `/api/v1` URL 전환 | BE route, FE endpoint, 호환성 처리 |
| `chore/ci-flyway-swagger` | CI, migration, API 문서 도입 | Gradle, workflow, migration, Swagger 설정 |
| `feat/seller-domain` | 판매자 역할과 승인 흐름 구현 | `Role.SELLER`, `SellerProfile`, 관리자 승인 API |

## chore/ai-harness 규칙

현재 브랜치의 목적은 문서형 하네스 추가다.

허용:

- `AGENTS.md`
- `api.md`
- `commit.md`
- `FE_INTEGRATION.md`
- `BRANCH_STRATEGY.md`
- 기타 문서 파일

금지:

- Java 코드 변경
- FE 코드 변경
- Gradle dependency 추가
- Docker, application 설정 변경
- API URL 변경
- 응답 포맷 변경
- 테스트 수정

## 커밋 분리 기준

문서형 하네스도 한 커밋이 너무 커지면 나눈다.

예시:

```text
chore : #1 AI 기본 작업 규칙 문서 추가
chore : #1 FE BE 연동 규칙 문서 추가
chore : #1 브랜치 및 커밋 전략 문서 추가
```

## PR 기준

PR 본문에는 다음을 포함한다.

- 작업 목적
- 추가한 문서 목록
- 코드 동작 영향 없음 여부
- 실행한 검증
- 다음 작업

`chore/ai-harness` PR의 다음 작업 예시:

```text
다음 작업
- feat/fe-api-integration 브랜치에서 FE API client와 Product adapter 추가
- refactor/api-response-format 브랜치에서 공통 응답 포맷 도입 검토
```
