package com.earlybird.ticket.venue.common.exception;

import com.earlybird.ticket.common.entity.constant.Code;

public class VenueCreateException extends AbstractVenueException {
    public VenueCreateException() {
        super("공연장 생성 시 문제가 발생했습니다.", Code.BAD_REQUEST);
    }
}
