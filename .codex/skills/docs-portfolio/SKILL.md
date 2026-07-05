---
name: docs-portfolio
description: Use when a domain feature is complete and the work should be summarized for Notion, project docs, or portfolio material.
---

# Goal

완성된 도메인 기능을 기술 문서와 포트폴리오 문장으로 정리한다. 과장된 수치나 실제로 검증하지 않은
성과는 쓰지 않는다.

# Standard Schema

## 1. Collect

- 요구사항
- 변경된 도메인
- 구현 파일
- API
- DB, Redis, S3 영향
- 테스트와 빌드 결과
- 트러블슈팅
- 남은 작업

## 2. Notion Draft

Notion 연결이 있으면 기존 Ecommerce 페이지 구조를 따른다. 연결이 없으면
`docs/domains/<domain>.md` 초안을 작성한다.

```md
# <Domain>

## 목적
## 사용자 흐름
## 구현 범위
## API
## 데이터 모델
## 권한
## 검증
## 트러블슈팅
## 다음 작업
```

## 3. Portfolio Draft

```md
## <기능명>

- 문제:
- 역할:
- 구현:
- 기술:
- 선택 이유:
- 검증:
- 배운 점:
- 다음 개선:
```

## 4. Rules

- 직접 구현한 범위만 쓴다.
- 측정하지 않은 개선율을 쓰지 않는다.
- 운영 데이터, 비밀정보, 개인정보를 쓰지 않는다.
- 다음 작업을 숨기지 않는다.
- 코드 변경 커밋과 문서 커밋은 필요하면 분리한다.
