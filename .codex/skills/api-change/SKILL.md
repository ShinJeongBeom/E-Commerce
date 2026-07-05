---
name: api-change
description: Use when adding or changing Ecommerce backend REST APIs, DTOs, controller mappings, response formats, or FE-facing API contracts.
---

# Goal

API 변경을 현재 구현과 목표 계약으로 분리하고, FE-BE 계약이 깨지지 않게 구현·검증·문서화를 수행한다.

# Standard Schema

## 1. Read

- `AGENTS.md`
- `api.md`
- `ARCHITECTURE.md`
- `SECURITY.md`
- 변경 도메인의 Controller, DTO, Service, Entity
- FE 연동이 있으면 `FE_INTEGRATION.md`

## 2. Classify

- 현재 API 유지 작업인지 확인한다.
- `/api/v1` 전환 작업인지 확인한다.
- `ApiResponse.payload` 공통 응답 전환 작업인지 확인한다.
- 인증 필요 API인지 공개 API인지 확인한다.

## 3. Design

- URL은 resource 중심으로 작성한다.
- Controller는 Request DTO를 command로 변환한다.
- UseCase 또는 Service result를 Response DTO로 변환한다.
- Entity를 직접 반환하지 않는다.
- 에러, 상태 코드, 권한 실패를 설계에 포함한다.

## 4. Implement

- 기존 패키지 구조와 naming을 따른다.
- validation은 HTTP boundary에서 수행한다.
- 비즈니스 규칙은 Service 계층에 둔다.
- Repository를 Controller에서 직접 호출하지 않는다.

## 5. Verify

- 관련 단위 테스트
- Controller 계약 테스트
- 인증·인가 실패 테스트
- 필요 시 `./gradlew test`
- 영향이 크면 `./gradlew build`

## 6. Document

- `api.md` 또는 Swagger/OpenAPI 문서를 갱신한다.
- FE 영향이 있으면 `FE_INTEGRATION.md`를 갱신한다.
- PR 본문에 현재 계약, 변경 계약, 다음 작업을 적는다.
