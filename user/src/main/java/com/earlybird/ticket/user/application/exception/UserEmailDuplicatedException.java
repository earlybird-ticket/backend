package com.earlybird.ticket.user.application.exception;

import com.earlybird.ticket.common.entity.constant.Code;

public class UserEmailDuplicatedException extends AbstractUserException {

    public UserEmailDuplicatedException() {
        super("이미 사용중인 이메일입니다.", Code.BAD_REQUEST);
    }
}
