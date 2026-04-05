import http from 'k6/http';
import {SharedArray} from 'k6/data';
import {check, sleep} from 'k6';
import {Trend, Counter, Rate} from 'k6/metrics';

const USER_TOKENS_PATH = __ENV.USER_TOKENS_PATH || '../data/user_tokens.json';

const users = new SharedArray('users', function () {
  const parsed = JSON.parse(open(USER_TOKENS_PATH));
  if (!Array.isArray(parsed) || parsed.length === 0) {
    throw new Error(`USER_TOKENS_PATH has no users: ${USER_TOKENS_PATH}`);
  }
  return parsed;
});

const BASE_URL = __ENV.BASE_URL || 'http://localhost:18086';
const VERSION = __ENV.VERSION || 'default';

const GRAFANA_URL = __ENV.GRAFANA_URL;
const SCRAPE_INTERVAL = 15;

const CONCERT_SEQUENCE_ID = '2d6d2381-a295-46fa-b798-a891f523c726';
const START_CHAR = __ENV.START_CHAR || 'A';
const END_CHAR = __ENV.END_CHAR || 'Z';

const seatLookupDuration = new Trend('seat_lookup_duration', true);
const seatCheckDuration = new Trend('seat_check_duration', true);
const seatPreemptDuration = new Trend('seat_preempt_duration', true);
const seatPreemptFlowDuration = new Trend('seat_preempt_flow_duration', true);

const seatLookupHttpSuccessRate = new Rate('seat_lookup_http_success_rate');
const seatCheckHttpSuccessRate = new Rate('seat_check_http_success_rate');
const seatPreemptHttpSuccessRate = new Rate('seat_preempt_http_success_rate');

const seatLookupEmptyRate = new Rate('seat_lookup_empty_rate');
const seatCheckRejectedRate = new Rate('seat_check_rejected_rate');

const finalSeatPreemptSuccessRate = new Rate('final_seat_preempt_success_rate');
const seatPreemptRejectedRate = new Rate('seat_preempt_rejected_rate');

const seatLookupSuccessCount = new Counter('seat_lookup_success_count');
const seatCheckSuccessCount = new Counter('seat_check_success_count');
const finalSeatPreemptSuccessCount = new Counter(
    'final_seat_preempt_success_count');

export const options = {
  discardResponseBodies: true,
  scenarios: {
    seat_preempt_flow: {
      executor: 'shared-iterations',
      vus: 1000,
      iterations: 1000,
      maxDuration: '30s',
      gracefulStop: '5s',
    },
  },
};

function buildSectionList() {
  const result = [];
  const startCode = START_CHAR.charCodeAt(0);
  const endCode = END_CHAR.charCodeAt(0);

  if (startCode > endCode) {
    throw new Error(
        `START_CHAR must be <= END_CHAR: ${START_CHAR} > ${END_CHAR}`);
  }

  for (let code = startCode; code <= endCode; code++) {
    result.push(String.fromCharCode(code));
  }

  return result;
}

function randomIndex(length) {
  return Math.floor(Math.random() * length);
}

function pickRandomSection(sectionList) {
  return sectionList[randomIndex(sectionList.length)];
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

function requestSeatList(headers, concertSequenceId, section) {
  return http.get(
      `${BASE_URL}/api/v1/external/seats/${concertSequenceId}/sections/${section}`,
      {
        headers,
        responseType: 'text',
        tags: {step: 'seat_lookup', name: 'seat_lookup'}
      }
  );
}

function requestSeatCheck(headers, concertSequenceId, seatInstanceIdList) {
  return http.post(
      `${BASE_URL}/api/v1/external/seats/check`,
      JSON.stringify({
        concert_sequence_id: concertSequenceId,
        seat_instance_id_list: seatInstanceIdList,
      }),
      {headers, tags: {step: 'seat_check', name: 'seat_check'}}
  );
}

function requestSeatPreempt(headers, payload) {
  return http.post(
      `${BASE_URL}/api/v1/external/seats/preempt`,
      JSON.stringify(payload),
      {headers, tags: {step: 'seat_preempt', name: 'seat_preempt'}}
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
  postAnnotation('좌석 서비스 부하 테스트 시작', ['load-test', 'start'])
}

export function teardown() {
  sleep(SCRAPE_INTERVAL)
  postAnnotation('좌석 서비스 부하 테스트 종료', ['load-test', 'end'])
}

export default function () {
  const user = users[(__VU - 1 + __ITER) % users.length];
  const headers = {
    'X-User-Passport': JSON.stringify({
      userId: user.user_id,
      userRole: 'USER'
    }),
    'Content-Type': 'application/json',
    'Accept-Encoding': 'gzip'
  };

  const flowStart = Date.now();

  const sectionList = buildSectionList();
  const currentSection = pickRandomSection(sectionList);

  const lookupStart = Date.now();
  const lookupRes = requestSeatList(headers, CONCERT_SEQUENCE_ID,
      currentSection);
  seatLookupDuration.add(Date.now() - lookupStart);

  const lookupBody = safeJson(lookupRes);
  const lookupHttpOk = check(lookupRes, {
    'seat lookup status is 200': (r) => r.status === 200,
  });
  seatLookupHttpSuccessRate.add(lookupHttpOk);

  if (!lookupHttpOk || !lookupBody?.data?.seat_list) {
    seatPreemptFlowDuration.add(Date.now() - flowStart);
    finalSeatPreemptSuccessRate.add(false);
    return;
  }

  const seatList = lookupBody.data.seat_list;
  const lookupEmpty = seatList.length === 0;
  seatLookupEmptyRate.add(lookupEmpty);

  if (lookupEmpty) {
    seatPreemptFlowDuration.add(Date.now() - flowStart);
    finalSeatPreemptSuccessRate.add(false);
    return;
  }

  seatLookupSuccessCount.add(1);

  const selectedSeats = [seatList[randomIndex(seatList.length)]];
  const concertId = lookupBody.data.concert_id;
  const concertSequenceId = lookupBody.data.concert_sequence_id;

  const seatInstanceIdList = selectedSeats.map((seat) => seat.seat_instance_id);

  const checkStart = Date.now();
  const checkRes = requestSeatCheck(headers, concertSequenceId,
      seatInstanceIdList);
  seatCheckDuration.add(Date.now() - checkStart);

  const checkHttpOk = check(checkRes, {
    'seat check passed with 200': (r) => r.status === 200,
  });
  seatCheckHttpSuccessRate.add(checkHttpOk);

  const checkRejected = !checkHttpOk;
  seatCheckRejectedRate.add(checkRejected);

  if (!checkHttpOk) {
    seatPreemptFlowDuration.add(Date.now() - flowStart);
    finalSeatPreemptSuccessRate.add(false);
    return;
  }

  seatCheckSuccessCount.add(1);

  const selectedSeatsPayload = selectedSeats.map((seat) => ({
    seat_instance_id: seat.seat_instance_id,
    seat_row: seat.row,
    seat_col: seat.col,
    seat_grade: lookupBody.data.grade,
    seat_price: seat.price,
  }));

  const preemptPayload = {
    user_name: user.name ?? 'tester',
    concert_id: concertId,
    concert_name: '2026 Earlybird 콘서트',
    concert_sequence_id: concertSequenceId,
    concert_sequence_start_datetime: '2026-03-15T19:00:00',
    concert_sequence_end_datetime: '2026-03-15T21:00:00',
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

  const preemptHttpOk = check(preemptRes, {
    'seat preempt status is 2xx': (r) => is2XX(r),
  });
  seatPreemptHttpSuccessRate.add(preemptHttpOk);
  const preemptRejected = preemptRes && preemptRes.status === 409;
  seatPreemptRejectedRate.add(preemptRejected);

  const finalSuccess = preemptHttpOk;
  finalSeatPreemptSuccessRate.add(finalSuccess);

  if (finalSuccess) {
    finalSeatPreemptSuccessCount.add(1);
  }

  seatPreemptFlowDuration.add(Date.now() - flowStart);
}
