package com.earlybird.ticket.payment.application.service.exception;

import com.earlybird.ticket.common.entity.constant.Code;
import com.earlybird.ticket.common.exception.AbstractException;

public class AbstractPaymentException extends AbstractException {

    public AbstractPaymentException(String message, Code code) {
        super(message, code);
    }
}
