package com.earlybird.ticket.reservation.common.exception;

import com.earlybird.ticket.common.entity.constant.Code;
import com.earlybird.ticket.common.exception.AbstractException;

public class LockFailedException extends AbstractException {
    public LockFailedException() {
        super("Failed To Earn Lcok",
              Code.BAD_REQUEST);
    }
}
