# Infrastructure and CI Guide

## Docker Compose

- 로컬 영속 데이터는 Docker Compose의 MySQL을 단일 기준으로 사용한다. Redis는 캐시·인증 등 보조 저장소다.
- 이미지 버전을 고정하고 `latest` 태그를 사용하지 않는다.
- healthcheck, 볼륨, 네트워크, 포트, 문자셋과 타임존을 명시한다.
- MySQL 데이터는 named volume에 저장해 컨테이너 재생성 후에도 유지한다.
- 비밀번호는 환경변수 또는 gitignore된 로컬 env 파일로 주입한다.
- Redis 목적에 맞게 영속화, 메모리 한도와 eviction 정책을 명시한다.
- Testcontainers를 도입한 뒤에는 해당 테스트에 불필요하게 compose를 시작하지 않는다.

변경 후 최소 검증:

```bash
docker compose config
docker compose up -d
docker compose ps
```

시작한 서비스는 healthcheck와 애플리케이션 연결을 확인한다. 작업 종료 시 기존에 실행 중이던
사용자 컨테이너를 임의로 종료하지 않는다.

## DB 계정과 권한

- 애플리케이션 계정은 대상 스키마의 최소 권한만 가진다.
- 가능하면 마이그레이션 계정과 런타임 계정을 분리한다.
- 런타임 계정에 전역 `*.*`, `GRANT OPTION`과 사용자 관리 권한을 주지 않는다.
- 런타임 계정은 필요한 `SELECT`, `INSERT`, `UPDATE`, `DELETE`만 부여하고 DDL 권한은 migration 계정으로 분리한다.
- 로컬·테스트 초기화 스크립트는 반복 실행 가능해야 한다.
- 적용 후 `SHOW GRANTS FOR '<user>'@'%'` 또는 동등한 명령으로 실제 권한을 확인한다.
- 필요한 CRUD가 성공하고 허용되지 않은 스키마 접근이 실패하는지 테스트한다.
- 운영·공유 DB의 계정 생성과 권한 변경은 SQL과 영향을 제시하고 승인 후 실행한다.

## 애플리케이션 이미지와 EC2/Nginx

- Dockerfile은 multi-stage build, 최소 런타임 이미지와 비루트 사용자를 우선한다.
- JVM 메모리 옵션, 컨테이너 종료 신호와 graceful shutdown을 설정한다.
- Nginx는 TLS 종료, 전달 header, 업로드 크기와 타임아웃을 명시한다.
- EC2 security group은 필요한 포트만 허용하고 MySQL·Redis를 인터넷에 공개하지 않는다.
- 애플리케이션 로그와 데이터는 컨테이너 수명에 종속되지 않게 관리한다.

## GitHub Actions

- PR에서 compile, unit test, integration test, static analysis와 build를 자동 실행한다.
- 캐시는 lock/build 파일을 키로 사용하고 검증을 생략하는 용도로 사용하지 않는다.
- AWS·DB·배포 자격 증명은 GitHub Secrets 또는 OIDC로 제공한다.
- fork PR의 비밀정보 노출과 임의 코드 실행 위험을 검토한다.
- 배포 job은 환경 승인, healthcheck와 실패 시 롤백 절차를 갖춘다.
- migration은 애플리케이션 호환 순서를 고려하고 동시에 여러 배포가 실행되지 않게 한다.

## 배포 완료 조건

- 이미지 빌드와 취약점 검사가 통과했다.
- 설정과 비밀정보가 환경별로 분리되었다.
- DB migration의 적용·롤백 또는 복구 절차가 준비되었다.
- 배포 후 healthcheck와 핵심 smoke test가 통과했다.
- 실패 시 직전 정상 버전으로 복구할 수 있다.
