# E-Commerce

## Git Workflow

본 프로젝트는 협업과 이력 관리를 위해 아래와 같은 Git 원칙을 기준으로 작업합니다.

### 브랜치 전략
- `main` : 안정 버전 및 배포 브랜치
- `develop` : 기능 통합 브랜치
- `feature/*` : 기능 개발 브랜치

예시
- `feature/login`
- `feature/product`
- `feature/order`

### 작업 흐름
1. `develop` 브랜치에서 기능 브랜치를 생성합니다.
2. 기능 단위로 작업 후 커밋합니다.
3. 작업 완료 후 `develop` 브랜치로 병합합니다.
4. 안정화가 완료되면 `main` 브랜치에 반영합니다.

### 커밋 메시지 규칙
커밋 메시지는 작업 내용을 명확하게 구분하기 위해 Prefix를 사용합니다.

- `feat` : 새로운 기능 추가
- `fix` : 버그 수정
- `refactor` : 코드 리팩토링
- `chore` : 설정, 빌드, 패키지 관리 등 기타 작업
- `docs` : 문서 수정
- `test` : 테스트 코드 추가 및 수정

예시
- `feat: 회원가입 API 구현`
- `fix: 로그인 예외 처리 수정`
- `refactor: 주문 서비스 로직 분리`
- `chore: Docker Compose 환경 설정 추가`

### 작업 원칙
- 기능 개발은 반드시 `feature` 브랜치에서 진행합니다.
- 의미 단위로 커밋을 분리합니다.
- 설정 파일 및 민감 정보는 `.gitignore`로 관리합니다.
- 환경 변수는 `.env` 또는 별도 설정 파일로 분리합니다.
