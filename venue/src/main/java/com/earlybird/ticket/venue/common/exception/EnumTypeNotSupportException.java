package com.earlybird.ticket.venue.common.exception;

import com.earlybird.ticket.common.entity.constant.Code;

public class EnumTypeNotSupportException extends AbstractVenueException{
    public EnumTypeNotSupportException() {
        super("지원하지 않는 타입 입니다.", Code.NOT_FOUND);
    }
}
