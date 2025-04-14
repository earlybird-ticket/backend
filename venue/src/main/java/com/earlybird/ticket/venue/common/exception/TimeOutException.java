package com.earlybird.ticket.venue.common.exception;

import com.earlybird.ticket.common.entity.constant.Code;

public class TimeOutException extends AbstractVenueException {
    public TimeOutException() {
        super("제한 시간이 초과되었습니다. 다시 시도해 주세요", Code.SEAT_CONFIRM_FAIL);
    }
}
