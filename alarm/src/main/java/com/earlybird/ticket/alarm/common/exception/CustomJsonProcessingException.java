package com.earlybird.ticket.alarm.common.exception;

import com.earlybird.ticket.common.entity.constant.Code;
import com.earlybird.ticket.common.exception.AbstractException;

public class CustomJsonProcessingException extends AbstractException {
    public CustomJsonProcessingException() {
        super("json process exception",
              Code.BAD_REQUEST);
    }
}
