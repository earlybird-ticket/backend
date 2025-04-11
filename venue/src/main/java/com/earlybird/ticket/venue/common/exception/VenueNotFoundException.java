package com.earlybird.ticket.venue.common.exception;

import com.earlybird.ticket.common.entity.constant.Code;

public class VenueNotFoundException extends AbstractVenueException {
    public VenueNotFoundException() {
        super("해당하는 공연장이 없습니다.", Code.NOT_FOUND);
    }
}
