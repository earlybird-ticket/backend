package com.earlybird.ticket.payment.application.service.exception;

import com.earlybird.ticket.common.entity.constant.Code;
import java.util.UUID;

public class PaymentDuplicatedException extends AbstractPaymentException {

    public PaymentDuplicatedException(UUID reservationId) {
        super("이미 처리된 결제입니다. = " + reservationId, Code.CONFLICT);
    }
}
