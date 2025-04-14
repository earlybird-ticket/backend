package com.earlybird.ticket.payment.application.service.exception;

import com.earlybird.ticket.common.entity.constant.Code;

public class JsonDeserializationException extends AbstractPaymentException {

    public JsonDeserializationException(String message) {
        super("Event deserialization Exception" + message, Code.BAD_REQUEST);
    }
}
