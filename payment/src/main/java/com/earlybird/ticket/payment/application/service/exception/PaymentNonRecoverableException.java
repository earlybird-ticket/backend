package com.earlybird.ticket.payment.application.service.exception;

import com.earlybird.ticket.common.entity.constant.Code;

public class PaymentNonRecoverableException extends AbstractPaymentException {

    public PaymentNonRecoverableException() {
        super("결제 메시지 처리 실패", Code.RECOVERABLE);
    }
}
