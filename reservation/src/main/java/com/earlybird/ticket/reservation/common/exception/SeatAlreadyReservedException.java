package com.earlybird.ticket.reservation.common.exception;

import com.earlybird.ticket.common.entity.constant.Code;
import com.earlybird.ticket.common.exception.AbstractException;

public class SeatAlreadyReservedException extends AbstractException {
    public SeatAlreadyReservedException() {
        super("seat already reserved",
              Code.BAD_REQUEST);
    }
}
