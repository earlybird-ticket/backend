package com.earlybird.ticket.alarm.common.exception;

import com.earlybird.ticket.common.entity.constant.Code;
import com.earlybird.ticket.common.exception.AbstractException;

public class RecoverableReservationException extends AbstractException {
    public RecoverableReservationException() {
        super("Failed to earn Lock",
              Code.RECOVERABLE);
    }
}
