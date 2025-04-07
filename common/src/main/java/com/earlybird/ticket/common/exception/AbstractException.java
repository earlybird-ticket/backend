package com.earlybird.ticket.common.exception;

import com.earlybird.ticket.common.entity.constant.Code;
import lombok.Getter;

@Getter
public abstract class AbstractException extends RuntimeException {

    private final Code code;

    protected AbstractException(String message,
                                Code code) {
        super(message);
        this.code = code;
    }


}