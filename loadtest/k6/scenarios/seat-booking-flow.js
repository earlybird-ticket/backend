import http from 'k6/http';
import {SharedArray} from 'k6/data';
import {check, sleep} from 'k6';
import {Trend, Counter, Rate} from 'k6/metrics';

const USER_TOKENS_PATH = __ENV.USER_TOKENS_PATH || '../data/user_tokens.json';
const CDN_MANIFEST_PATH =
    __ENV.CDN_MANIFEST_PATH || '../data/cdn-seats/manifest.json';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:18081';
const START_CHAR = __ENV.START_CHAR || 'A';
const END_CHAR = __ENV.END_CHAR || 'Z';
const CONCERT_SEQUENCE_ID = '2d6d2381-a295-46fa-b798-a891f523c726';

const SCRAPE_INTERVAL = 15;
const GRAFANA_URL = __ENV.GRAFANA_URL;
const VERSION = __ENV.VERSION || 'default';

const MAX_SEAT_ACQUIRE_RETRIES = 5;
const MAX_PAYMENT_CREATION_RETRIES = 5;
const MAX_PAYMENT_CONFIRM_RETRIES = 5;

const users = new SharedArray('users', function () {
  const parsed = JSON.parse(open(USER_TOKENS_PATH));
  if (!Array.isArray(parsed) || parsed.length === 0) {
    throw new Error(`USER_TOKENS_PATH has no users: ${USER_TOKENS_PATH}`);
  }
  return parsed;
});

const cdnManifest = JSON.parse(open(CDN_MANIFEST_PATH));
if (!Array.isArray(cdnManifest) || cdnManifest.length === 0) {
  throw new Error(`CDN_MANIFEST_PATH has no sections: ${CDN_MANIFEST_PATH}`);
}

const cdnUrlMap = {};
for (const item of cdnManifest) {
  cdnUrlMap[item.section] = item.cdn_url;
}

const baseSectionList = buildSectionList(
    cdnManifest.map((item) => item.section)
);

// per request duration
const seatLookupDuration = new Trend('seat_lookup_duration', true);
const cdnLookupDuration = new Trend('cdn_lookup_duration', true);
const seatCheckDuration = new Trend('seat_check_duration', true);
const seatPreemptDuration = new Trend('seat_preempt_duration', true);

const paymentCreationDuration = new Trend('payment_creation_duration', true);
const paymentConfirmDuration = new Trend('payment_confirm_duration', true);

// flow duration
const seatAcquireFlowDuration = new Trend('seat_acquire_flow_duration', true);

const paymentCreationFlowDuration = new Trend('payment_creation_flow_duration',
    true);
const paymentConfirmFlowDuration = new Trend('payment_confirm_flow_duration',
    true);
const paymentFlowDuration = new Trend('payment_flow_duration', true);

const endToEndFlowDuration = new Trend('end_to_end_flow_duration', true);
const successfulEndToEndFlowDuration =
    new Trend('successful_end_to_end_flow_duration', true);

// success rate
const seatLookupHttpSuccessRate = new Rate('seat_lookup_http_success_rate');
const cdnLookupHttpSuccessRate = new Rate('cdn_lookup_http_success_rate');
const seatCheckHttpSuccessRate = new Rate('seat_check_http_success_rate');
const seatPreemptHttpSuccessRate = new Rate('seat_preempt_http_success_rate');
const paymentCreationHttpSuccessRate =
    new Rate('payment_creation_http_success_rate');
const paymentConfirmHttpSuccessRate =
    new Rate('payment_confirm_http_success_rate');

const seatAcquireSuccessRate = new Rate('seat_acquire_success_rate');
const seatAcquireFirstTrySuccessRate =
    new Rate('seat_acquire_first_try_success_rate');

const paymentCreationSuccessRate = new Rate('payment_creation_success_rate');
const paymentConfirmSuccessRate = new Rate('payment_confirm_success_rate');
const finalBookingSuccessRate = new Rate('final_booking_success_rate');

// rejection / empty rate
// seatLookupEmptyRate의 모수는 "lookup HTTP 성공 + available_indexes 필드 존재" 응답입니다.
// 즉 전체 lookup 대비 empty 비율이 아니라, 유효 lookup 응답 중 empty 비율로 해석합니다.
const seatLookupEmptyRate = new Rate('seat_lookup_empty_rate');

// seatPreemptRejectedRate는 preempt 요청까지 도달한 경우 중 409 응답 비율입니다.
// lookup/CDN/check 단계에서 중단된 요청은 포함하지 않습니다.
const seatPreemptRejectedRate = new Rate('seat_preempt_rejected_rate');

// counter
// 퍼널 구조:
// seatLookupSuccessCount  -> available_indexes.length > 0 인 lookup 성공
// cdnLookupSuccessCount   -> seats 포함 CDN 응답 확보 성공
// cdnLookupFailureCount   -> CDN 응답 실패로 seat acquire 루프가 중단된 경우
// seatCheckSuccessCount   -> seat check 200 응답 성공
// seatPreemptSuccessCount -> preempt 성공 후 reservationId 확보까지 완료된 경우
// seatAcquireSuccessCount -> 좌석 확보 플로우 전체 최종 성공
const seatLookupSuccessCount = new Counter('seat_lookup_success_count');
const cdnLookupSuccessCount = new Counter('cdn_lookup_success_count');
const cdnLookupFailureCount = new Counter('cdn_lookup_failure_count');
const seatCheckSuccessCount = new Counter('seat_check_success_count');
const seatPreemptSuccessCount = new Counter('seat_preempt_success_count');

const seatAcquireSuccessCount = new Counter('seat_acquire_success_count');

const paymentCreationSuccessCount =
    new Counter('payment_creation_success_count');
const paymentConfirmSuccessCount =
    new Counter('payment_confirm_success_count');

const finalBookingSuccessCount = new Counter('final_booking_success_count');

// retry trend
const seatAcquireRetryTrend = new Trend('seat_acquire_retry_count', true);
const paymentCreationRetryTrend =
    new Trend('payment_creation_retry_count', true);
const paymentConfirmRetryTrend = new Trend('payment_confirm_retry_count', true);

export const options = {
  discardResponseBodies: true,
  scenarios: {
    booking_flow: {
      executor: 'shared-iterations',
      vus: 1000,
      iterations: 1000,
      maxDuration: '30s',
      gracefulStop: '5s',
    },
  },
};

function buildSectionList(manifestSections) {
  const startCode = START_CHAR.charCodeAt(0);
  const endCode = END_CHAR.charCodeAt(0);

  if (startCode > endCode) {
    throw new Error(
        `START_CHAR must be <= END_CHAR: ${START_CHAR} > ${END_CHAR}`);
  }

  const filtered = manifestSections.filter((section) => {
    const code = section.charCodeAt(0);
    return code >= startCode && code <= endCode;
  });

  if (filtered.length === 0) {
    throw new Error(
        `No sections matched range: START_CHAR=${START_CHAR}, END_CHAR=${END_CHAR}`
    );
  }

  return filtered;
}

function randomIndex(length) {
  return Math.floor(Math.random() * length);
}

function pickRandomSection(sectionList) {
  return sectionList[randomIndex(sectionList.length)];
}

function removeSection(sectionList, section) {
  return sectionList.filter((s) => s !== section);
}

function is2XX(res) {
  return res && res.status >= 200 && res.status < 300;
}

function safeJson(res) {
  try {
    return res.json();
  } catch (e) {
    return null;
  }
}

function calculateAmount(selectedSeats) {
  return selectedSeats.reduce((sum, seat) => sum + Number(seat.price), 0);
}

function requestCdnSeatLayout(cdnUrl) {
  return http.get(
      cdnUrl,
      {
        responseType: 'text',
        tags: {step: 'cdn_lookup', name: 'cdn_lookup'}
      }
  );
}

function requestAvailableSeatIndexes(headers, concertSequenceId, section) {
  return http.get(
      `${BASE_URL}/api/v1/seats/${concertSequenceId}/sections/${section}`,
      {
        headers,
        responseType: 'text',
        tags: {step: 'seat_lookup', name: 'seat_lookup'}
      }
  );
}

function requestSeatCheck(headers, concertSequenceId, seatInstanceIdList) {
  return http.post(
      `${BASE_URL}/api/v1/seats/check`,
      JSON.stringify({
        concert_sequence_id: concertSequenceId,
        seat_instance_id_list: seatInstanceIdList,
      }),
      {headers, tags: {step: 'seat_check'}}
  );
}

function requestSeatPreempt(headers, payload) {
  return http.post(
      `${BASE_URL}/api/v1/seats/preempt`,
      JSON.stringify(payload),
      {
        headers,
        responseType: 'text',
        tags: {step: 'seat_preempt'}
      }
  );
}

function requestPaymentCreation(headers, payload) {
  return http.post(
      `${BASE_URL}/api/v1/payments`,
      JSON.stringify(payload),
      {
        headers,
        responseType: 'text',
        tags: {step: 'payment_create'}
      }
  );
}

function requestPaymentConfirm(headers, payload) {
  return http.post(
      `${BASE_URL}/api/v1/payments/confirm`,
      JSON.stringify(payload),
      {headers, tags: {step: 'payment_confirm'}}
  );
}

function postAnnotation(text, tags = []) {
  if (!GRAFANA_URL) {
    return null;
  }

  const payload = {
    text: `[${VERSION}] ${text}`,
    tags: [...tags, `version:${VERSION}`],
    time: Date.now()
  };

  const res = http.post(`http://admin:admin@${GRAFANA_URL}/api/annotations`,
      JSON.stringify(payload), {
        headers: {
          'Content-Type': 'application/json',
        },
        timeout: '3s',
        tags: {step: 'grafana_annotation'},
      });

  if (!is2XX(res)) {
    console.log(`Failed to post annotation: ${res.status}`)
    return null;
  }
}

// 테스트 시작 시 어노테이션
export function setup() {
  postAnnotation('부하 테스트 시작', ['load-test', 'start']);
}

export function teardown() {
  sleep(SCRAPE_INTERVAL);
  postAnnotation('부하 테스트 종료', ['load-test', 'end']);
}

export default function () {
  // request context
  const user = users[(__VU - 1 + __ITER) % users.length];
  const headers = {
    Authorization: `Bearer ${user.token}`,
    'Content-Type': 'application/json',
    'Accept-Encoding': 'gzip'
  };

  // ── Phase 1: 좌석 확보 ──────────────────────────────────────────
  // lookup → cdn → check → preempt
  const flowStart = Date.now();

  let seatAcquireSuccess = false;
  let seatAcquireRetryCount = 0;

  let reservationId = null;
  let selectedSeats = null;

  let concertId = null;
  let sectionList = [...baseSectionList];
  let concertSequenceId = CONCERT_SEQUENCE_ID;

  const seatAcquireStart = Date.now();

  while (
      !seatAcquireSuccess
      && seatAcquireRetryCount < MAX_SEAT_ACQUIRE_RETRIES
      && sectionList.length > 0
      ) {
    // seat lookup
    const currentSection = pickRandomSection(sectionList);

    const lookupStart = Date.now();
    const lookupRes = requestAvailableSeatIndexes(
        headers, concertSequenceId, currentSection
    );
    seatLookupDuration.add(Date.now() - lookupStart);

    const lookupBody = safeJson(lookupRes);
    const lookupHttpOk = check(lookupRes, {
      'seat lookup status is 200': (r) => r.status === 200,
    });
    seatLookupHttpSuccessRate.add(lookupHttpOk);

    if (!lookupHttpOk || !lookupBody?.data?.available_indexes) {
      seatAcquireRetryCount += 1;
      continue;
    }

    const availableIndexes = lookupBody.data.available_indexes;
    if (availableIndexes.length === 0) {
      seatLookupEmptyRate.add(true);
      sectionList = removeSection(sectionList, currentSection);
      seatAcquireRetryCount += 1;
      continue;
    }
    seatLookupSuccessCount.add(1);
    seatLookupEmptyRate.add(false);

    // cdn layout resolve
    const cdnUrl = cdnUrlMap[currentSection];
    if (!cdnUrl) {
      throw new Error(`Missing CDN URL for section=${currentSection}`);
    }

    const cdnStart = Date.now();
    const cdnRes = requestCdnSeatLayout(cdnUrl);
    cdnLookupDuration.add(Date.now() - cdnStart);

    const cdnHttpOk = check(cdnRes, {
      'cdn lookup status is 200': (r) => r.status === 200,
    });
    cdnLookupHttpSuccessRate.add(cdnHttpOk);

    const cdnBody = safeJson(cdnRes);
    if (!cdnHttpOk || !cdnBody?.seats) {
      cdnLookupFailureCount.add(1);
      break;
    }
    cdnLookupSuccessCount.add(1);

    const seatList = cdnBody.seats;
    const selectedSeatIndex =
        availableIndexes[randomIndex(availableIndexes.length)];
    selectedSeats = [seatList[selectedSeatIndex]];

    if (!selectedSeats[0]) {
      throw new Error(
          `Invalid seat index mapping: section=${currentSection}, seatList.length=${seatList.length}`
      );
    }

    concertId = cdnBody.concert_id;
    concertSequenceId = cdnBody.concert_sequence_id;

    const seatInstanceIdList = selectedSeats.map(
        (seat) => seat.seat_instance_id
    );

    // seat check
    const checkStart = Date.now();
    const checkRes = requestSeatCheck(
        headers, concertSequenceId, seatInstanceIdList
    );
    seatCheckDuration.add(Date.now() - checkStart);

    const checkHttpOk = check(checkRes, {
      'seat check passed with 200': (r) => r.status === 200,
    });
    seatCheckHttpSuccessRate.add(checkHttpOk);

    if (!checkHttpOk) {
      seatAcquireRetryCount += 1;
      continue;
    }
    seatCheckSuccessCount.add(1);

    // seat preempt
    const selectedSeatsPayload = selectedSeats.map((seat) => ({
      seat_instance_id: seat.seat_instance_id,
      seat_row: seat.row,
      seat_col: seat.col,
      seat_grade: 'R',
      seat_price: seat.price,
    }));

    const preemptPayload = {
      user_name: user.name ?? 'tester',
      concert_id: concertId,
      concert_name: '2026 Earlybird 콘서트',
      concert_sequence_id: concertSequenceId,
      concert_sequence_start_datetime: '2026-04-15T19:00:00',
      concert_sequence_end_datetime: '2026-04-15T21:00:00',
      concert_sequence_status: 'OPEN',
      venue_id: 'b09ea418-f1f8-4732-b317-0d5b2a1ff45c',
      venue_area: '강남구',
      venue_location: '서울',
      seat_list: selectedSeatsPayload,
      hall_id: '3ac1ade1-4068-443e-8c88-6b517b00f2e8',
      hall_name: '오페라극장',
      hall_floor: 1,
    };

    const preemptStart = Date.now();
    const preemptRes = requestSeatPreempt(headers, preemptPayload);
    seatPreemptDuration.add(Date.now() - preemptStart);

    const preemptBody = safeJson(preemptRes);
    const preemptHttpOk = check(preemptRes, {
      'seat preempt status is 2xx': (r) => is2XX(r),
    });
    seatPreemptHttpSuccessRate.add(preemptHttpOk);
    seatPreemptRejectedRate.add(preemptRes?.status === 409);

    if (!preemptHttpOk) {
      seatAcquireRetryCount += 1;
      continue;
    }

    reservationId = preemptBody?.data ?? null;
    if (!reservationId) {
      seatAcquireRetryCount += 1;
      continue;
    }

    seatPreemptSuccessCount.add(1);
    seatAcquireSuccess = true;
  }

  // 좌석 획득 결과 취합
  const seatAcquireEnd = Date.now();
  const seatAcquireElapsed = seatAcquireEnd - seatAcquireStart;

  seatAcquireFlowDuration.add(seatAcquireElapsed);
  seatAcquireRetryTrend.add(seatAcquireRetryCount);
  seatAcquireFirstTrySuccessRate.add(
      seatAcquireSuccess && seatAcquireRetryCount === 0
  );
  seatAcquireSuccessRate.add(seatAcquireSuccess);

  if (!seatAcquireSuccess) {
    finalBookingSuccessRate.add(false);
    endToEndFlowDuration.add(seatAcquireEnd - flowStart);
    return;
  }

  seatAcquireSuccessCount.add(1);

  const amount = calculateAmount(selectedSeats);

  // ── Phase 2: 결제 생성 ──────────────────────────────────────────
  const creationPhase = runPaymentCreationPhase(
      headers, user, amount, reservationId
  );

  if (!creationPhase.success) {
    paymentFlowDuration.add(creationPhase.elapsed);
    finalBookingSuccessRate.add(false);
    endToEndFlowDuration.add(Date.now() - flowStart);
    return;
  }
  paymentCreationSuccessCount.add(1);

  // ── Phase 3: 결제 확정 ──────────────────────────────────────────
  const confirmPhase = runPaymentConfirmPhase(
      headers, amount, reservationId
  );
  const paymentEnd = Date.now();

  paymentFlowDuration.add(creationPhase.elapsed + confirmPhase.elapsed);

  if (!confirmPhase.success) {
    finalBookingSuccessRate.add(false);
    endToEndFlowDuration.add(paymentEnd - flowStart);
    return;
  }
  paymentConfirmSuccessCount.add(1);

  // 최종 예매 결과 취합
  finalBookingSuccessRate.add(true);
  finalBookingSuccessCount.add(1);
  endToEndFlowDuration.add(paymentEnd - flowStart);
  successfulEndToEndFlowDuration.add(paymentEnd - flowStart);
};

function runPaymentCreationPhase(headers, user, amount, reservationId) {
  let success = false;
  let retryCount = 0;

  const payload = {
    user_id: user.user_id,
    user_email: user.email,
    user_name: user.name ?? 'tester',
    amount,
    reservation_id: reservationId,
    order_name: '2026 Earlybird',
  };

  const start = Date.now();

  while (!success && retryCount < MAX_PAYMENT_CREATION_RETRIES) {

    const reqStart = Date.now();
    const res = requestPaymentCreation(headers, payload);
    paymentCreationDuration.add(Date.now() - reqStart);

    const ok = check(res, {'payment creation status is 2xx': (r) => is2XX(r)});
    paymentCreationHttpSuccessRate.add(ok);

    if (ok) {
      success = true;
      break;
    }

    retryCount += 1;
  }

  const elapsed = Date.now() - start;

  paymentCreationFlowDuration.add(elapsed);
  paymentCreationRetryTrend.add(retryCount);
  paymentCreationSuccessRate.add(success);

  return {success, retryCount, elapsed};
}

function runPaymentConfirmPhase(headers, amount, reservationId) {
  let success = false;
  let retryCount = 0;

  const payload = {amount, order_id: reservationId};

  const start = Date.now();

  while (!success && retryCount < MAX_PAYMENT_CONFIRM_RETRIES) {
    const reqStart = Date.now();
    const res = requestPaymentConfirm(headers, payload);
    paymentConfirmDuration.add(Date.now() - reqStart);

    const ok = check(res, {'payment confirm status is 2xx': (r) => is2XX(r)});
    paymentConfirmHttpSuccessRate.add(ok);

    if (ok) {
      success = true;
      break;
    }
    retryCount += 1;
  }

  const elapsed = Date.now() - start;

  paymentConfirmFlowDuration.add(elapsed);
  paymentConfirmRetryTrend.add(retryCount);
  paymentConfirmSuccessRate.add(success);

  return {success, retryCount, elapsed};
}