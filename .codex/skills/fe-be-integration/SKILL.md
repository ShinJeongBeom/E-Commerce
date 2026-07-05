---
name: fe-be-integration
description: Use when connecting the Vite React frontend to the Spring Boot backend, replacing mock data, adding API clients, or aligning DTOs.
---

# Goal

Ecommerce FE mock 또는 임시 화면을 현재 BE API와 안전하게 연결한다. `/api/v1`, 공통 응답 전환,
도메인 확장은 같은 작업에 섞지 않는다.

# Standard Schema

## 1. Read

- Backend: `/Users/shinjeongbeom/SpringProject/E-Commerce`
- Frontend: `/Users/shinjeongbeom/Java/SpringProject/E-Commerce-FE`
- `AGENTS.md`
- `FE_INTEGRATION.md`
- `api.md`
- 관련 BE Controller와 DTO
- 관련 FE component, API call, type

## 2. Confirm Current Contract

- 현재 BE URL을 확인한다.
- 현재 BE response shape를 확인한다.
- 인증 필요 여부를 확인한다.
- FE form field와 BE request DTO가 일치하는지 확인한다.

## 3. Plan

- `PLANS.md` 형식으로 목표, 범위, 제외 항목, 검증을 적는다.
- 현재 구현에 맞추는 작업과 향후 목표를 분리한다.
- adapter가 필요한 DTO를 명시한다.

## 4. Implement

- API base URL은 `VITE_API_BASE_URL`을 사용한다.
- 화면 컴포넌트에 fetch 로직을 계속 쌓지 않는다.
- BE DTO와 FE 화면 모델 사이에 adapter를 둔다.
- JWT 인증 API에는 `Authorization: Bearer <accessToken>`을 전달한다.
- 로딩, 에러, 빈 상태를 처리한다.

## 5. Verify

- FE build 또는 typecheck를 실행한다.
- 가능하면 BE 서버를 띄워 실제 API로 화면을 확인한다.
- BE 서버가 없을 때의 에러 상태를 확인한다.
- 인증 API는 로그인 전후 동작을 확인한다.

## 6. Commit

- FE 연동 구현과 문서 갱신은 필요하면 분리 커밋한다.
- 메시지는 `commit.md` 형식을 따른다.
- PR에는 작업 내용, 검증, 다음 작업을 적는다.
