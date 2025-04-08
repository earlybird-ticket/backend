package com.earlybird.ticket.venue.common.exception;

import com.earlybird.ticket.common.entity.constant.Code;
import com.earlybird.ticket.common.exception.AbstractException;

public abstract class AbstractVenueException extends AbstractException {

    public AbstractVenueException(String message, Code code) {
        super(message, code);
    }
}
