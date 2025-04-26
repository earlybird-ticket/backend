package com.earlybird.ticket.venue.common.exception;

import com.earlybird.ticket.common.entity.constant.Code;

public class SeatReturnFailException extends AbstractVenueException {
    public SeatReturnFailException() {
        super("좌석 반환 과정에서 예외가 발생하였습니다.", Code.SEAT_RETURN_FAIL);
    }
}
