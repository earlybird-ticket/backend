package com.earlybird.ticket.alarm.common.exception;

import com.earlybird.ticket.common.entity.constant.Code;
import com.earlybird.ticket.common.exception.AbstractException;

public class NonRecoverableReservationException extends AbstractException {
    public NonRecoverableReservationException() {
        super("none recoverable reservation",
              Code.NON_RECOVERABLE);
    }
}
