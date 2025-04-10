package com.earlybird.ticket.venue.common.exception;

import com.earlybird.ticket.common.entity.constant.Code;

public class SeatNotFoundException extends AbstractVenueException {
    public SeatNotFoundException() {
        super("좌석을 찾을 수 없습니다.", Code.NOT_FOUND);
    }
}
