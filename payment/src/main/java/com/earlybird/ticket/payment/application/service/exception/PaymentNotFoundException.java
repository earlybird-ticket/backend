package com.earlybird.ticket.payment.application.service.exception;

import com.earlybird.ticket.common.entity.constant.Code;

public class PaymentNotFoundException extends AbstractPaymentException {

    public PaymentNotFoundException() {
        super("결제 내역을 불러올 수 없습니다.", Code.NOT_FOUND);
    }
}
