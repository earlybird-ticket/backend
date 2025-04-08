package com.earlybird.ticket.auth.application;

import com.earlybird.ticket.auth.application.dto.commander.CreateSellerCommand;
import com.earlybird.ticket.auth.application.dto.commander.CreateUserCommand;
import com.earlybird.ticket.auth.application.dto.commander.GetUserIdPasswordRoleCommand;
import com.earlybird.ticket.auth.application.dto.commander.UserInfoCommand;
import com.earlybird.ticket.common.entity.PassportDto;

public interface UserClient {
    void createUser(CreateUserCommand createUserCommand);

    void createSeller(CreateSellerCommand createSellerCommand);

    UserInfoCommand getUserInfo(GetUserIdPasswordRoleCommand getUserIdPasswordRoleCommand);

    void withdraw(PassportDto passportDto);
}
