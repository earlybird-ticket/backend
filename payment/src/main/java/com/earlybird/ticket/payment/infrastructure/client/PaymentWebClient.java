package com.earlybird.ticket.payment.infrastructure.client;


import com.earlybird.ticket.payment.application.service.PaymentClient;
import com.earlybird.ticket.payment.application.service.dto.command.ConfirmPaymentCommand;
import com.earlybird.ticket.payment.application.service.dto.command.UpdatePaymentCancelCommand;
import com.earlybird.ticket.payment.application.service.dto.command.UpdatePaymentCommand;
import com.earlybird.ticket.payment.application.service.exception.PaymentAbortException;
import com.earlybird.ticket.payment.application.service.exception.PaymentCancelException;
import com.earlybird.ticket.payment.infrastructure.client.dto.response.ProcessPaymentCancelClientResponse;
import com.earlybird.ticket.payment.infrastructure.client.dto.response.ProcessPaymentConfirmClientResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class PaymentWebClient implements PaymentClient {

    private static final String IDEMPOTENCY_KEY = "Idempotency-key";

    private static final String TOSS_PAYMENT_URI_PREFIX = "https://api.tosspayments.com/v1/payments";

    private final String tossClientSecret;

    private final WebClient webClient;

    public PaymentWebClient(
        @Value("${toss.secret-key}") String tossClientSecret,
        WebClient webClient
    ) {
        this.tossClientSecret = tossClientSecret;
        this.webClient = webClient;
    }

    @Override
    public UpdatePaymentCommand confirmPayment(ConfirmPaymentCommand command,
        UUID paymentId) {
        log.info("토스페이 결제 요청 보냄");
        ProcessPaymentConfirmClientResponse receipt = webClient.post()
            .uri(TOSS_PAYMENT_URI_PREFIX + "/confirm")
            .headers(httpHeaders -> {
                httpHeaders.add(HttpHeaders.AUTHORIZATION, getAuthorizationValue());
                httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                httpHeaders.add(IDEMPOTENCY_KEY, command.reservationId().toString());
            })
            .bodyValue(command)
            .exchangeToMono(response -> {
                if (response.statusCode().is2xxSuccessful()) {
                    return response.bodyToMono(ProcessPaymentConfirmClientResponse.class);
                }

                if (response.statusCode().is4xxClientError()) {
                    return response.bodyToMono(String.class)
                        .flatMap(errorBody -> {
                            log.error("결제 승인 클라이언트 에러 : {}", errorBody);
                            return Mono.error(new PaymentAbortException());
                        });
                }
                // 이제는 토스 서버 문제
                return response.bodyToMono(String.class)
                    .flatMap(errorBody -> {
                        log.error("결제 승인 서버 에러 : {}", errorBody);
                        return Mono.error(new PaymentAbortException());
                    });
            })
            .block();

        log.info("토스페이 결제 성공 = {}", receipt);

        return receipt.toCommand();
    }

    private String getAuthorizationValue() {
        byte[] encodedAuth = Base64.getEncoder()
            .encode(
                (tossClientSecret + ":").getBytes(StandardCharsets.UTF_8)
            );
        return "Basic " + new String(encodedAuth);
    }

    @Override
    public UpdatePaymentCancelCommand cancelPayment(String paymentKey, UUID reservationId) {
        log.info("토스페이 결제 취소 요청");

        ProcessPaymentCancelClientResponse result = webClient.post()
            .uri(TOSS_PAYMENT_URI_PREFIX + "/{paymentKey}/cancel", paymentKey)
            .headers(httpHeaders -> {
                httpHeaders.add(HttpHeaders.AUTHORIZATION, getAuthorizationValue());
                httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                httpHeaders.add(IDEMPOTENCY_KEY, reservationId.toString());
            })
            .bodyValue(Map.of("cancelReason", "예약취소"))
            .exchangeToMono(response -> {
                if (response.statusCode().is2xxSuccessful()) {
                    return response.bodyToMono(ProcessPaymentCancelClientResponse.class);
                } else if (response.statusCode().is4xxClientError()) {
                    return response.bodyToMono(String.class)
                        .flatMap(errorBody -> {
                            log.error("토스페이 결제 취소 클라이언트 에러: {}", errorBody);
                            return Mono.error(new PaymentCancelException());
                        });
                } else {
                    return response.bodyToMono(String.class)
                        .flatMap(errorBody -> {
                            log.error("토스페이 결제 취소 서버 에러: {}", errorBody);
                            return Mono.error(new PaymentCancelException());
                        });
                }
            })
            .block();

        log.info("토스페이 취소 = {}", result);

        if (result.reservationId() == null || !result.reservationId().equals(reservationId)) {
            throw new PaymentCancelException();
        }

        return result.toUpdatePaymentCancelCommand();
    }
}
