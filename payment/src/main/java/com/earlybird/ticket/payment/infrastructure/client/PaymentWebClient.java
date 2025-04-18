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
            .retrieve()
            .bodyToMono(ProcessPaymentConfirmClientResponse.class)
            .block();

        log.info("토스페이 결제 성공 = {}", receipt);

        if (receipt == null) {
            log.error("결제 실패 = {}", paymentId);
            throw new PaymentAbortException();
        }

        // TODO: 추후 가상계좌의 경우 만료기간 까지 입금 X시, 취소 처리
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
            .retrieve()
            .bodyToMono(ProcessPaymentCancelClientResponse.class)
            .block();

        log.info("토스페이 취소 = {}", result);

        if (result.reservationId() == null || !result.reservationId().equals(reservationId)) {
            throw new PaymentCancelException();
        }

        return result.toUpdatePaymentCancelCommand();
    }
}
