# Loadtest Branch

이 브랜치는 좌석 조회 경로 최적화와 venue 부하 테스트 실험 정리용 브랜치입니다.
A/B/C-1/C-2A 비교, 1500 동시 진입 검증, Tomcat 입구 병목 확인 및 병목 이동 확인 결과를 포함합니다.

## Branch roles

- `test/seat-a-baseline`: 기존 조회 방식 기준선
- `test/seat-b-baseline`: Redis 조회 최적화 기준선
- `loadtest/seat-reservation-scenario`: C-1, C-2A 실험 및 최종 정리 브랜치

## Experiment variants

- `A`: Redis에서 좌석 ID 목록을 조회한 뒤, 각 좌석 상세 필드를 다시 읽어 서버가 최종 좌석 목록을 조합해 반환하는 기존 방식
- `B`: 조회 시 필요한 좌석 상세를 Redis에 미리 직렬화해 두고, 서버가 이를 바로 읽어 반환하는 방식
- `C-1`: 서버는 `available_indexes`만 반환하고, k6가 미리 preload한 정적 좌석 데이터와 매핑해 최종 좌석을 구성하는 방식
- `C-2A`: 서버는 `available_indexes`만 반환하고, 정적 좌석 데이터는 CDN에서 직접 조회해 매핑하는 방식

## Final takeaways

- A/B/C-1/C-2A 비교 결과, 운영 현실성과 전체 flow 기준에서 C-2A를 가장 유력한 후보로 판단했습니다.
- 1500 동시 진입 테스트에서 초기 병목은 venue Tomcat 입구 구간에서 확인됐습니다.
- Tomcat 입구 설정 조정 후 lookup tail이 완화되었고, 이후 병목이 check/preempt 경쟁 구간으로 이동했습니다.

## Test environment

- 실제 부하 테스트는 NCP 환경에서 수행했습니다.
- loadtest 디렉토리의 compose는 애플리케이션과 관측 구성 중심으로 사용했고, 데이터 저장소는 NCP 관리형 자원을 사용했습니다.
- 이는 테스트용 애플리케이션과 데이터 저장소를 분리해 로컬 자원 경쟁과 간섭으로 인한 결과 왜곡을 줄이기 위한 선택입니다.

## Main scripts

- 토큰 발급: `loadtest/scripts/generate_tokens.sh`
- 부하 테스트용 venue 기준 데이터 적재: `loadtest/scripts/seed_venue_test_data.sh`
- 기본 venue 성능 테스트(A/B): `loadtest/scripts/venue_perf_test.sh`
- C/C-2A 실험 실행: `loadtest/scripts/venue_perf_test_C_variant.sh`
- CDN 좌석 데이터 다운로드: `loadtest/scripts/download_cdn_seats.sh`

## Required components

venue 성능 테스트 실행 및 관측을 위해 아래 구성이 필요합니다.

- 애플리케이션: `venue`
- 부하 발생기: `k6`
- 모니터링: `grafana`, `prometheus`, `node-exporter`, `redis-exporter`, `postgres-exporter`
- 데이터 저장소: `PostgreSQL`, `Redis`

`auth`는 테스트용 토큰 발급 시에만 필요합니다.

`gateway`, `reservation`, `payment`, `eureka`는 전체 예매 흐름 테스트 시 필요하며, venue 단독 성능 테스트에서는 필수는 아닙니다.

NCP 환경에서는 데이터 저장소와 애플리케이션 인스턴스 접근을 위해 bastion을 사용할 수 있으나, 이는 테스트 구성 요소라기보다 네트워크 접근 경로에 해당합니다.

## Monitoring references

- 앱 스크레이프 타깃 예시: `loadtest/docker/monitoring/prometheus/targets/apps-targets-example.json`
- 노드 익스포터 스크레이프 타깃 예시: `loadtest/docker/monitoring/prometheus/targets/node-targets-example.json`

## Environment file examples

- 공통 기본값은 `.env.base`, 모니터링 관련 값은 `.env.monitoring`, 서비스별 예시는 `.env.<domain>.example`로 관리합니다.
- 실제 실행 전에는 대상 파일을 복사해 환경에 맞는 값으로 수정해 사용합니다.

## Test data prerequisites

부하 테스트 실행 전 아래 준비가 필요합니다.

- 테스트 환경을 먼저 기동한 뒤 `seed_venue_test_data.sh`를 실행해 부하 테스트용 venue 기준 데이터를 적재해야 합니다.
- 이 스크립트는 `p_venue`, `p_hall`, `p_seat`, `p_seat_instance` 데이터를 적재합니다.
- C-1, C-2A 실험 전에는 `download_cdn_seats.sh`를 실행해 CDN 좌석 정적 데이터와 manifest 파일을 준비해야 합니다.

## Execution order

실행 순서 예시: 테스트 환경 기동 → (토큰이 없는 경우) generate_tokens.sh 실행 → seed_venue_test_data.sh 실행 → (C-1/C-2A인 경우) download_cdn_seats.sh 실행 → venue 성능 테스트 실행

## Image tags

이미지 태그는 두 종류로 관리합니다.

- 의미 태그: 사람이 이미지 용도를 바로 파악하기 위한 태그
- sha 태그: 특정 시점의 이미지를 재현하고 추적하기 위한 고정 태그

예시:
- `auth:loadtest-generate-tokens` → 부하 테스트용 토큰 발급 이미지
- `auth:sha-<short-sha>` → 위 이미지와 동일한 빌드의 재현용 태그

현재 기준 태그:
- auth: `loadtest-generate-tokens`
- gateway/payment/reservation/eureka: `loadtest`
- venue:
  - A 기준선: `seat-a-baseline`, `sha-57e6a33`
  - B 기준선: `seat-b-baseline`, `sha-68f4650`
  - C 기준선: `loadtest-cdn`

재현이 필요한 경우 의미 태그와 함께 해당 시점의 sha 태그를 사용합니다.

### Build tag example

```bash
docker build -f auth/Dockerfile \
  --build-arg MODULE_NAME=auth \
  -t <docker-registry-path>/auth:loadtest-generate-tokens \
  -t <docker-registry-path>/auth:sha-<short-sha> \
  --platform linux/amd64 \
  .
```

```bash
docker build -f venue/Dockerfile \
  --build-arg MODULE_NAME=venue \
  -t <docker-registry-path>/venue:loadtest-cdn \
  -t <docker-registry-path>/venue:sha-<short-sha> \
  --platform linux/amd64 \
  .
```

- 첫 번째 태그는 용도 파악용입니다.
- 두 번째 태그는 동일 이미지의 재현용입니다.

## Reference

- 실험 결과 및 상세 해석은 wiki 문서에 정리 예정