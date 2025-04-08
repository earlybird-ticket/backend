package com.earlybird.ticket.user.application.exception;

import com.earlybird.ticket.common.entity.constant.Code;

public class UserNotFoundException extends AbstractUserException {

    public UserNotFoundException() {
        super("회원 정보를 찾을 수 없습니다.", Code.NOT_FOUND);
    }
}
