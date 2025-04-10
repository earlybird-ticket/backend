package com.earlybird.ticket.venue.common.exception;

import com.earlybird.ticket.common.entity.constant.Code;

public class SeatUnavailableException extends AbstractVenueException {
    public SeatUnavailableException() {
        super("이미 선점된 좌석입니다.", Code.CONFLICT);
    }
}
