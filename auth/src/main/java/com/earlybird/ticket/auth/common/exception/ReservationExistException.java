package com.earlybird.ticket.auth.common.exception;

import com.earlybird.ticket.common.entity.constant.Code;
import com.earlybird.ticket.common.exception.AbstractException;

public class ReservationExistException extends AbstractException {

    public ReservationExistException() {
        super("Reservation exist, treat first",
              Code.UNAUTHORIZED);
    }
}
