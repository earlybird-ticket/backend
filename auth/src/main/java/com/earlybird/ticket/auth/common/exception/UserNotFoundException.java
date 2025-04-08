package com.earlybird.ticket.auth.common.exception;

import com.earlybird.ticket.common.entity.constant.Code;
import com.earlybird.ticket.common.exception.AbstractException;

public class UserNotFoundException extends AbstractException {

    public UserNotFoundException() {
        super("User Not Found",
              Code.BAD_REQUEST);
    }
}
