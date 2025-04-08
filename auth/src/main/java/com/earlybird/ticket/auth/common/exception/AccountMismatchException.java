package com.earlybird.ticket.auth.common.exception;

import com.earlybird.ticket.common.entity.constant.Code;
import com.earlybird.ticket.common.exception.AbstractException;

public class AccountMismatchException extends AbstractException {

    public AccountMismatchException() {
        super("Wrong Account, Please check again",
              Code.UNAUTHORIZED);
    }
}
