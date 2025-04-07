package com.earlybird.ticket.auth.infrastructure.client;

import com.earlybird.ticket.auth.application.UserClient;
import com.earlybird.ticket.auth.application.dto.commander.CreateSellerCommand;
import com.earlybird.ticket.auth.application.dto.commander.CreateUserCommand;
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
}
