# Plans Guide

# Plan: 관리자 메인 대시보드

## 목표
- 첨부된 관리자 메인 화면 구조에 맞춰 ADMIN 로그인 후 관리자 콘솔을 표시한다.
- BE는 관리자 메인에 필요한 집계 데이터를 `/admin/dashboard`로 제공한다.
- FE는 기존 쇼핑몰 화면 구성은 유지하고, ADMIN 계정만 관리자 메인 화면으로 분기한다.

## 현재 상태
- 현재 브랜치: BE/FE `feature/admin-main-dashboard`
- 관련 저장소: BE `/Users/shinjeongbeom/SpringProject/E-Commerce`, FE `/Users/shinjeongbeom/Java/SpringProject/E-Commerce-FE`
- 기존 구현: BE는 `/admin/sellers`와 판매자 승인 API가 있고, FE develop은 관리자 화면이 없다.
- 제약: `/api/v1` 전환과 `ApiResponse.payload` 전환은 이번 작업에서 제외한다.

## 범위
- 포함: `/admin/dashboard` API, 관리자 메인 FE 화면, ADMIN 로그인 화면 분기, API 문서 갱신
- 제외: 실제 게시판·문의·신고 도메인 구현, 관리자 메뉴별 상세 페이지, 운영 배포

## 작업 순서
1. 조사: 하네스 문서, 현재 BE/FE 브랜치와 관리자 관련 코드 확인
2. 구현: BE 관리자 대시보드 DTO/Service/Controller 추가
3. 구현: FE 관리자 대시보드 화면과 로그인 role 분기 추가
4. 문서: `api.md`, `FE_INTEGRATION.md` 갱신
5. 테스트: BE test, FE lint/build 실행
6. 커밋: BE/FE 의도 단위로 분리

## 검증
- 실행할 명령: `./gradlew test`
- 실행할 명령: `npm run lint`
- 실행할 명령: `npm run build`
- 확인할 API: `GET /admin/dashboard`
- 수동 확인: ADMIN 로그인 시 관리자 메인 화면 진입

## 커밋 분리
- `feat : #1 관리자 메인 대시보드 API 추가`
- `feat : #1 관리자 메인 화면 추가`
- `chore : #1 관리자 메인 API 계약 문서화`

## 리스크
- 남은 위험: 게시판, 문의, 신고 도메인이 아직 없어 일부 카운트와 목록은 기본값이다.
- 롤백 방법: 각 저장소에서 해당 브랜치 커밋을 revert하거나 develop 브랜치로 전환한다.

## 목적

`PLANS.md`는 매 작업을 시작하기 전에 목표, 범위, 순서, 검증 방법을 고정하는 실행 계획서다.
AI는 작업이 커질수록 이 파일을 먼저 갱신하고, 구현 중 결정이 바뀌면 계획도 함께 갱신한다.

## 사용 시점

- FE-BE 연동처럼 여러 파일과 저장소를 함께 다룰 때
- API 계약, DB 스키마, 인증, Redis, 배포 설정이 바뀔 때
- 30분 이상 걸릴 가능성이 있는 작업일 때
- 커밋을 여러 개로 나눠야 할 가능성이 있을 때

간단한 문서 오탈자나 단일 테스트 수정처럼 작은 작업은 최종 보고에 계획을 요약해도 된다.

## 작성 형식

```md
# Plan: <작업 이름>

## 목표
- 사용자가 기대하는 완료 상태

## 현재 상태
- 현재 브랜치:
- 관련 저장소:
- 기존 구현:
- 제약:

## 범위
- 포함:
- 제외:

## 작업 순서
1. 조사
2. 구현
3. 테스트
4. 문서
5. 커밋

## 검증
- 실행할 명령:
- 확인할 API:
- 수동 확인:

## 커밋 분리
- <prefix> : #<issue-number> <내용>

## 리스크
- 남은 위험:
- 롤백 방법:
```

## 운영 규칙

- 계획은 실제 코드보다 앞서야 한다.
- 계획은 고정 문서가 아니라 작업 중 갱신되는 문서다.
- 계획이 바뀌면 바뀐 이유를 한 줄로 남긴다.
- 사용자가 명확히 지시한 범위를 넘는 항목은 `제외`에 적는다.
- 구현 전에는 `검증`에 실행 가능한 명령을 적는다.
- 완료 전에는 계획의 각 항목이 실제 결과와 일치하는지 확인한다.

## FE-BE 연동 계획 예시

```md
# Plan: FE 상품 목록 백엔드 연동

## 목표
- FE mock 상품 목록을 BE `GET /products` 응답으로 교체한다.

## 현재 상태
- BE: `/products` raw DTO 응답 제공
- FE: `App.tsx` mock 상품 배열 사용
- 인증: 상품 조회는 공개 API

## 범위
- 포함: API client, ProductResponse 타입, adapter, 상품 목록 fetch
- 제외: `/api/v1`, ApiResponse payload, 장바구니 연동

## 작업 순서
1. FE 현재 상품 모델과 mock 사용 위치 확인
2. `VITE_API_BASE_URL` 적용
3. Product API client 작성
4. `ProductResponse -> Product` adapter 작성
5. 상품 목록을 API 데이터로 교체
6. 로딩, 에러, 빈 목록 상태 확인

## 검증
- `npm run build`
- 브라우저에서 상품 목록 노출 확인
- BE 서버 미실행 시 에러 상태 확인

## 커밋 분리
- feat : #1 상품 목록 백엔드 연동
```
