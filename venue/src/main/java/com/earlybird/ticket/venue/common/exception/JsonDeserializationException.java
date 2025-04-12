package com.earlybird.ticket.venue.common.exception;

import com.earlybird.ticket.common.entity.constant.Code;

public class JsonDeserializationException extends AbstractVenueException {
    public JsonDeserializationException(String message) {
        super("Event deserialization Exception" + message, Code.BAD_REQUEST);
    }
}
