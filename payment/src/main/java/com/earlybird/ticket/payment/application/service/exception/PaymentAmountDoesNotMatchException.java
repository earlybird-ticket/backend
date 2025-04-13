package com.earlybird.ticket.payment.application.service.exception;

import com.earlybird.ticket.common.entity.constant.Code;

public class PaymentAmountDoesNotMatchException extends AbstractPaymentException {

    public PaymentAmountDoesNotMatchException() {
        super("비정상적인 결제 요청입니다.", Code.BAD_REQUEST);
    }
}
