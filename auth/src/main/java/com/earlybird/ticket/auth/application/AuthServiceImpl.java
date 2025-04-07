package com.earlybird.ticket.auth.application;

import com.earlybird.ticket.auth.application.dto.commander.CreateSellerCommand;
import com.earlybird.ticket.auth.application.dto.commander.CreateUserCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserClient userClient;

    @Override
    public void createUser(CreateUserCommand createUserCommand) {

        //유저 회원가입 데이터 담아 보내기
        userClient.createUser(createUserCommand);

    }

    @Override
    public void createSeller(CreateSellerCommand createSellerCommand) {

        //유저 회원가입 데이터 담아 보내기
        userClient.createSeller(createSellerCommand);
    }

}
