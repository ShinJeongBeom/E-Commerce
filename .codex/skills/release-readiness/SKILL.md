---
name: release-readiness
description: Use before commit, push, PR creation, merge, or deployment to verify tests, secrets, docs, and operational risks.
---

# Goal

커밋, PR, 배포 전에 변경 범위와 검증 증거를 확인하고 위험을 명확히 남긴다.

# Standard Schema

## 1. Inspect

- `git status -sb`
- `git diff --stat`
- `git diff --name-only`
- staged 상태라면 `git diff --cached --stat`
- staged 상태라면 `git diff --cached --name-only`

## 2. Scope Check

- 요청 범위와 다른 파일이 포함되었는지 확인한다.
- 사용자 기존 변경이 섞였는지 확인한다.
- 코드, 설정, 문서, 테스트, 인프라 변경을 분류한다.

## 3. Safety Check

- `SAFETY.md`를 확인한다.
- 비밀정보와 개인정보를 scan한다.
- 운영 DB, 운영 Redis, AWS, GitHub Actions secret 영향이 있는지 확인한다.
- destructive command가 필요한 경우 승인 없이 진행하지 않는다.

## 4. Verification

- Backend: `./gradlew test`
- 영향이 크면 `./gradlew build`
- Docker 변경 시 `docker compose config`
- FE 변경 시 build 또는 typecheck
- API 변경 시 계약 테스트 또는 수동 요청 확인

## 5. Checklist

- `CHECKLIST.md`를 기준으로 통과, 미검증, 제외 항목을 정리한다.
- 미검증 항목은 이유와 잔여 위험을 적는다.

## 6. Final Report

최종 보고에는 다음을 포함한다.

- 변경 요약
- 실행한 검증 명령과 결과
- 커밋 해시와 메시지
- 문서 갱신
- 미실행 검증
- 다음 작업
