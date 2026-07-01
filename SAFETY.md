# Safety Guide

## 목적

AI가 프로젝트를 안전하게 다루기 위한 금지 작업과 승인 필요 작업을 정의한다. 코드 품질만큼
중요한 것은 사용자 변경, 데이터, 비밀정보, 배포 환경을 보존하는 것이다.

## 기본 원칙

- 사용자가 요청하지 않은 파일을 되돌리지 않는다.
- 작업 전 `git status -sb`로 기존 변경사항을 확인한다.
- 파괴적 명령은 목적, 영향, 복구 방법을 설명하고 명시적 승인을 받은 뒤 실행한다.
- 운영 DB, 운영 Redis, AWS, GitHub Actions secret은 읽거나 변경하지 않는다.
- 실제 비밀번호, JWT secret, AWS key, DB 접속정보를 문서나 커밋에 남기지 않는다.

## 명시적 승인 없이는 금지

- `git reset --hard`
- `git clean -fd`
- `git push --force`
- 기존 커밋 amend 또는 rebase
- 운영 DB DDL, DML, 권한 변경
- 운영 Redis key 삭제
- AWS S3 object 삭제
- GitHub Actions secret 변경
- 배포 실행
- migration rollback

## 승인 후에도 신중히 수행

- 로컬 DB 데이터 삭제
- Docker volume 삭제
- dependency major version 변경
- 보안 설정 완화
- CORS 허용 범위 확대
- JWT 만료 시간 변경
- 인증 필터 순서 변경

## 비밀정보 스캔 기준

커밋 전 다음 유형을 확인한다.

- `.env`, `.env.local`, `.properties` 안의 실제 secret
- `spring.datasource.password`
- `jwt.secret`
- `AWS_ACCESS_KEY_ID`
- `AWS_SECRET_ACCESS_KEY`
- private key block
- 실제 bearer token

환경변수 placeholder는 허용한다.

```text
${LOCAL_DB_PASSWORD}
${JWT_SECRET}
${AWS_ACCESS_KEY_ID}
```

## DB 안전 규칙

- 로컬 개발 DB와 운영 DB를 명확히 구분한다.
- `ddl-auto=create`, `create-drop`, `update`를 운영 설정으로 사용하지 않는다.
- 스키마 변경은 migration 도입 후 forward-only를 우선한다.
- 데이터 삭제 쿼리는 `WHERE` 조건, 대상 수, 백업 여부를 확인한다.
- 권한 부여는 최소 권한 원칙을 적용한다.

## Git 안전 규칙

- 커밋에는 하나의 큰 의도만 담는다.
- 사용자 변경과 AI 변경을 섞지 않는다.
- 이미 push된 커밋을 수정하려면 사용자 승인을 받는다.
- PR 생성 전 staged diff와 최신 커밋 파일 목록을 확인한다.
- push는 사용자가 요청했을 때만 수행한다.

## 사고 대응

문제가 생기면 즉시 다음을 기록한다.

- 실행한 명령
- 변경된 파일
- 실패 로그 핵심
- 되돌릴 수 있는 방법
- 사용자 승인이 필요한 다음 행동
