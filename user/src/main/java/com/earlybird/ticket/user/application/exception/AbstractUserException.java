package com.earlybird.ticket.user.application.exception;

import com.earlybird.ticket.common.entity.constant.Code;
import com.earlybird.ticket.common.exception.AbstractException;

public abstract class AbstractUserException extends AbstractException {

    protected AbstractUserException(String message,
        Code code) {
        super(message, code);
    }
}
