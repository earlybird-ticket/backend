package com.earlybird.ticket.payment.application.service.exception;

import com.earlybird.ticket.common.entity.constant.Code;

public class PaymentCancelClientFailedException extends PaymentCancelException {

    public PaymentCancelClientFailedException() {
        super("결제 중 [클라이언트]문제가 발생했습니다. 다시 시도해주세요.", Code.CONFLICT);
    }
}
