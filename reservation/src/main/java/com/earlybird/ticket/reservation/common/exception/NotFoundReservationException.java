package com.earlybird.ticket.reservation.common.exception;

import com.earlybird.ticket.common.entity.constant.Code;
import com.earlybird.ticket.common.exception.AbstractException;

public class NotFoundReservationException extends AbstractException {
    public NotFoundReservationException() {
        super("not found reservation",
              Code.NOT_FOUND);
    }
}
