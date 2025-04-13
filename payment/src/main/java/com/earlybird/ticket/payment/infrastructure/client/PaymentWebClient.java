package com.earlybird.ticket.payment.infrastructure.client;


import com.earlybird.ticket.payment.application.service.PaymentClient;
import com.earlybird.ticket.payment.application.service.dto.command.ConfirmPaymentCommand;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
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
    public void confirmPayment(ConfirmPaymentCommand command, UUID paymentId) {
        log.info("토스페이 결제 요청 보냄");
        String block = webClient.post()
            .uri("https://api.tosspayments.com/v1/payments/confirm")
            .headers(httpHeaders -> {
                httpHeaders.add(HttpHeaders.AUTHORIZATION, getAuthorizationValue());
                httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                //httpHeaders.add(IDEMPOTENCY_KEY, command.orderId().toString());
            })
            .bodyValue(command)
            .retrieve()
            .bodyToMono(String.class)
            .block();

        log.info("response = {}", block);
    }

    private String getAuthorizationValue() {
        byte[] encodedAuth = Base64.getEncoder()
            .encode(
                (tossClientSecret + ":").getBytes(StandardCharsets.UTF_8)
            );
        return "Basic " + new String(encodedAuth);
    }
}
