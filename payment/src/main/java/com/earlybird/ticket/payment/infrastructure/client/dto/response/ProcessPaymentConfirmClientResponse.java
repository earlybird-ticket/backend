package com.earlybird.ticket.payment.infrastructure.client.dto.response;

import com.earlybird.ticket.payment.application.service.dto.command.UpdatePaymentCommand;
import com.earlybird.ticket.payment.domain.entity.constant.PaymentMethod;
import com.earlybird.ticket.payment.domain.entity.constant.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import lombok.Builder;

@Builder
public record ProcessPaymentConfirmClientResponse(
    String method,
    String status,
    String paymentKey,

    @JsonProperty("approvedAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    OffsetDateTime approvedAt,
    // 가상계좌에서 사용
    @JsonProperty("requestedAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    OffsetDateTime requestedAt
) {

    private static final String VIRTUAL_ACCOUNT = "가상계좌";

    public UpdatePaymentCommand toCommand() {
        return UpdatePaymentCommand.builder()
            .paymentMethod(PaymentMethod.from(method))
            .status(PaymentStatus.from(status))
            .paymentKey(paymentKey)
            .approvedAt(transformDate())
            .build();
    }

    /* TODO : 결제 성공 시간과 요청시간 분리
        가상계좌의 경우 요청 시간에서 일정 시간 지난 경우 취소처리
       현재는 가상계좌의 경우 요청 시간으로 결제 성공 시간을 대신함
    */
    public LocalDateTime transformDate() {
        if (method.equals(VIRTUAL_ACCOUNT)) {
            return requestedAt.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        }

        return approvedAt.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime();
    }
}
