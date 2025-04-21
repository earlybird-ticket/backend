package com.earlybird.ticket.payment.application.service.exception;

import com.earlybird.ticket.common.entity.constant.Code;

public class PaymentInformationDoesNotMatchException extends AbstractPaymentException {

    public PaymentInformationDoesNotMatchException() {
        super("결제 정보가 일치하지 않습니다.", Code.BAD_REQUEST);
    }
}
