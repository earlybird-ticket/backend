package com.earlybird.ticket.payment.application.service.exception;

import com.earlybird.ticket.common.entity.constant.Code;

public class PaymentCancelException extends AbstractPaymentException {

    public PaymentCancelException() {
        super("결제 취소 중 문제가 발생했습니다. 다시 시도해주세요.", Code.CONFLICT);
    }
}
