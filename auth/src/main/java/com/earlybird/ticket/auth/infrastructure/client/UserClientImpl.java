package com.earlybird.ticket.auth.infrastructure.client;

import com.earlybird.ticket.auth.application.UserClient;
import com.earlybird.ticket.auth.application.dto.commander.CreateSellerCommand;
import com.earlybird.ticket.auth.application.dto.commander.CreateUserCommand;
import com.earlybird.ticket.auth.application.dto.commander.GetUserIdPasswordRoleCommand;
import com.earlybird.ticket.auth.application.dto.commander.UserInfoCommand;
import com.earlybird.ticket.auth.infrastructure.dto.payload.UserInfoClientResponse;
import com.earlybird.ticket.common.entity.PassportDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserClientImpl implements UserClient {
    private final UserFeignClient userFeignClient;

    @Override
    public void createUser(CreateUserCommand createUserCommand) {

        userFeignClient.createUser(createUserCommand);
    }

    @Override
    public void createSeller(CreateSellerCommand createSellerCommand) {
        userFeignClient.createSeller(createSellerCommand);
    }

    @Override
    public UserInfoCommand getUserInfo(
        GetUserIdPasswordRoleCommand getUserIdPasswordRoleCommand
    ) {

        return UserInfoClientResponse.toUserInfoCommandDto(
            userFeignClient.getUserInfo(getUserIdPasswordRoleCommand)
                .getData());
    }

    @Override
    public void withdraw(PassportDto passportDto) {
        userFeignClient.withdraw(passportDto);
    }
}
