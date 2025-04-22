package com.earlybird.ticket.venue.common.exception;

import com.earlybird.ticket.common.entity.constant.Code;

public class RedisException extends AbstractVenueException {
    public RedisException() {
        super("Redis Script Error", Code.REDIS_SCRIPT_ERROR);
    }
}
