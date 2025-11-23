package com.earlybird.ticket.payment.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mockwebserver3.Dispatcher;
import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import mockwebserver3.RecordedRequest;
import okhttp3.Headers;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MockTossPayServer {

    private final ObjectMapper objectMapper;
    private final MockWebServer mockWebServer = new MockWebServer();
    private final ConcurrentHashMap<UUID, String> processedReservation = new ConcurrentHashMap<>();
    private final DateTimeFormatter formatter =
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

    @PostConstruct
    public void init() throws IOException {
        mockWebServer.setDispatcher(new Dispatcher() {
            @NotNull
            @Override
            public MockResponse dispatch(@NotNull RecordedRequest recordedRequest)
                throws InterruptedException {

                String path = recordedRequest.getUrl().encodedPath();
                try {
                    String idempotencyKey = recordedRequest.getHeaders().get("Idempotency-key");

                    if (path.equals("/v1/payments/confirm")) {
                        UUID reservationId = UUID.fromString(idempotencyKey);
                        if (processedReservation.containsKey(reservationId)) {
                            return new MockResponse.Builder()
                                .code(409)
                                .headers(new Headers.Builder()
                                    .add("Content-Type", "application/json")
                                    .build()
                                )
                                .body(objectMapper.writeValueAsString(
                                    Map.of(
                                        "code", "-1",
                                        "msg", "이미 결제가 완료된 예약입니다.", "errorCode",
                                        "PAYMENT_EXISTING_PAYMENT"))
                                )
                                .build();
                        }

                        StringBuilder paymentKey = generatePaymentKey(reservationId);
                        processedReservation.put(reservationId, paymentKey.toString());

                        return new MockResponse.Builder()
                            .headers(new Headers.Builder()
                                .add("Content-Type", "application/json")
                                .build())
                            .code(200)
                            .body(objectMapper.writeValueAsString(
                                Map.of(
                                    "code", "0",
                                    "method", "간편결제",
                                    "requestedAt",
                                    ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("Asia/Seoul"))
                                        .format(formatter),
                                    "approvedAt",
                                    ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("Asia/Seoul"))
                                        .format(formatter),
                                    "status", "DONE",
                                    "paymentKey", paymentKey,
                                    "orderId", reservationId.toString()
                                )
                            ))
                            .build();

                    }
                } catch (JsonProcessingException e) {
                    log.error("변환 오류 발생 : {}", e.getMessage());
                    return new MockResponse.Builder()
                        .code(500)
                        .build();
                }

                return new MockResponse.Builder()
                    .code(404)
                    .build();
            }
        });
        mockWebServer.start();
    }

    @NotNull
    private static StringBuilder generatePaymentKey(UUID reservationId) {
        StringBuilder paymentKey = new StringBuilder();
        paymentKey.append("tviva").append(LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] res = md.digest(reservationId.toString().getBytes());

            for (int i = 0; i < 5; i++) {
                paymentKey.append(String.format("%02x", res[i]));
            }
        } catch (NoSuchAlgorithmException e) {
            log.error("알고리즘 없음.");
        }
        return paymentKey;
    }

    public String getUrl() {
        return mockWebServer.url("/v1/payments").toString();
    }
}
