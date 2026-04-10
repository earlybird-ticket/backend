# Loadtest Branch

이 브랜치는 좌석 조회 경로 최적화와 venue 부하 테스트 실험 정리용 브랜치입니다.
A/B/C-1/C-2A 비교, 1500 동시 진입 검증, Tomcat 입구 병목 확인 및 병목 이동 확인 결과를 포함합니다.

## Branch roles

- `test/seat-a-baseline`: 기존 조회 방식 기준선
- `test/seat-b-baseline`: Redis 조회 최적화 기준선
- `loadtest/seat-reservation-scenario`: CDN 계열(C-1, C-2A) 실험 및 loadtest 정리 브랜치

## Experiment variants

- `A`: Redis에서 좌석 ID 목록을 조회한 뒤, 좌석 상세 다시 다건 조회해 서버가 최종 좌석 목록을 조합해 반환하는 기존 방식
- `B`: 조회 시 필요한 좌석 상세를 Redis에 미리 직렬화해 두고, 서버가 이를 직접 조회해 반환하는 방식
- `C-1`: 서버는 `available_indexes`만 반환하고, k6가 테스트 시작 전에 preload한 정적 좌석 데이터와 런타임 응답을 로컬 매핑하는 방식
- `C-2A`: 서버는 `available_indexes`만 반환하고, k6가 정적 자원인 `manifest.json`으로 CDN URL을 확보한 뒤 런타임에 CDN을 직접 조회해 매핑하는 방식

C-1과 C-2A는 모두 서버가 `available_indexes`만 반환하는 동일한 CDN 계열 방식입니다.
차이는 정적 좌석 데이터를 테스트 시작 전에 preload하느냐, 런타임에 직접 조회하느냐에 있으며 이는 서버 구현 차이가 아닌 k6 스크립트 실행 전략 차이입니다.

## Final takeaways

- A/B/C-1/C-2A 비교 결과, flow p95와 preload/init 비용 기준에서 C-2A를 최종 기준안으로 판단했습니다.
- 1500 동시 진입 테스트에서 초기 병목은 venue Tomcat 입구 구간에서 확인됐습니다.
- Tomcat 입구 설정 조정 후 lookup tail이 완화되었고, 이후 병목이 check/preempt 경쟁 구간으로 이동했습니다.

## Test environment

- 실제 부하 테스트는 NCP 환경에서 수행했습니다.
- loadtest 디렉토리의 compose는 애플리케이션과 관측 구성 중심으로 사용했고, 데이터 저장소는 NCP 관리형 자원을 사용했습니다.
- 이는 테스트용 애플리케이션과 데이터 저장소를 분리해 로컬 자원 경쟁과 간섭으로 인한 결과 왜곡을 줄이기 위한 선택입니다.

## Main scripts

- 토큰 발급: `loadtest/scripts/generate_tokens.sh`
- 부하 테스트용 venue 기준 데이터 적재: `loadtest/scripts/seed_venue_test_data.sh`
- 테스트 전 venue 캐시 warmup: `loadtest/scripts/prepare_venue_cache.sh`
- 기본 venue 성능 테스트(A/B): `loadtest/scripts/venue_perf_test.sh`
- CDN 계열 실험(C-1/C-2A): `loadtest/scripts/venue_perf_test_C_variant.sh`
- 예매 전체 흐름 테스트: `loadtest/scripts/seat_booking_flow_test.sh`
- CDN 좌석 데이터 다운로드: `loadtest/scripts/download_cdn_seats.sh`

## Required components

venue 성능 테스트 실행 및 관측을 위해 아래 구성이 필요합니다.

- 애플리케이션: `venue`
- 부하 발생기: `k6`
- 모니터링: `grafana`, `prometheus`, `node-exporter`, `redis-exporter`, `postgres-exporter`
- 데이터 저장소: `PostgreSQL`, `Redis`

`auth`는 테스트용 토큰 발급 시에만 필요합니다.

`gateway`, `reservation`, `payment`, `eureka`는 전체 예매 흐름 테스트 시 필요하며, venue 단독 성능 테스트에서는 필수 구성은 아닙니다.

## Test prerequisites

부하 테스트 실행 전 아래 준비가 필요합니다.

- 테스트 환경을 먼저 기동한 뒤 `seed_venue_test_data.sh`를 실행해 부하 테스트용 venue 기준 데이터를 적재해야 합니다.
  - 이 스크립트는 `p_venue`, `p_hall`, `p_seat`, `p_seat_instance` 데이터를 적재합니다.
- A/B/C 계열 성능 비교 전에는 **Redis 캐시 상태를 초기화**해야 합니다.
  - Redis flush는 **테스트 전용 인스턴스에서만** 수행해야 합니다.
- 캐시 초기화 후 `prepare_venue_cache.sh`를 실행해 동일한 warmup 상태에서 테스트를 시작해야 합니다.
- C-1, C-2A 실험 전에는 `download_cdn_seats.sh`를 실행해 CDN 좌석 정적 데이터와 manifest 파일을 준비해야 합니다.

실행 순서 예시: 
`테스트 환경 기동` 
→ `(토큰이 없는 경우) generate_tokens.sh 실행` 
→ `seed_venue_test_data.sh 실행`
→ `(C-1/C-2A인 경우) download_cdn_seats.sh 실행` 
→ `Redis flush` 
→ `prepare_venue_cache.sh 실행` 
→ `venue 성능 테스트 실행`

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
  - A 기준선: `seat-a-baseline`, `sha-826d88d`
  - B 기준선: `seat-b-baseline`, `sha-5a6bd91`
  - C 계열 실험: `loadtest-cdn`
    - 실험 당시에는 HEAD 기반으로 사용해 별도 sha 태그를 고정하지 않았습니다.

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

- [좌석 조회 경로 최적화 비교 및 최종 채택안](https://github.com/earlybird-ticket/backend/wiki/%EC%A2%8C%EC%84%9D-%EC%A1%B0%ED%9A%8C-%EA%B2%BD%EB%A1%9C-%EC%B5%9C%EC%A0%81%ED%99%94-%EB%B9%84%EA%B5%90-%EB%B0%8F-%EC%B5%9C%EC%A2%85-%EC%B1%84%ED%83%9D%EC%95%88-%EC%A0%95%EB%A6%AC)
- [1,000명 동시 진입 기준 전체 예매 플로우 부하 테스트 결과](https://github.com/earlybird-ticket/backend/wiki/1000%EB%AA%85-%EB%8F%99%EC%8B%9C-%EC%A7%84%EC%9E%85-%EA%B8%B0%EC%A4%80-%EC%A0%84%EC%B2%B4-%EC%98%88%EB%A7%A4-%ED%94%8C%EB%A1%9C%EC%9A%B0-%EB%B6%80%ED%95%98-%ED%85%8C%EC%8A%A4%ED%8A%B8-%EA%B2%B0%EA%B3%BC)