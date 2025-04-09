package com.earlybird.ticket.auth.infrastructure.client;

import com.earlybird.ticket.auth.application.UserClient;
import com.earlybird.ticket.auth.application.dto.commander.CreateSellerCommand;
import com.earlybird.ticket.auth.application.dto.commander.CreateUserCommand;
import com.earlybird.ticket.auth.application.dto.commander.JoinUserCommand;
import com.earlybird.ticket.auth.application.dto.commander.UserInfoCommand;
import com.earlybird.ticket.auth.infrastructure.dto.payload.UserInfoClientResponse;
import com.earlybird.ticket.common.entity.PassportDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


/**
 * 1. 회원 가입 성공 시 데이터를 캐싱
 * 2. 로그인시 User서버가 장애가 난 경우
 * 2-1. 캐싱 데이터가 있는 경우(Cache Hit)
 * - 캐싱 데이터의 아이디 비밀번호와 유저가 입력한 아이디 비밀번호 일치 여부 조회
 * - 일치하는 경우 해당 값을 기준으로 토큰 발급
 * - 일치하지 않는 경우 500예외 또는 Feign예외 발생
 * 2-2. 캐싱 데이터가 없는 경우
 * - 어쩔수없이 500예외 또는 Feign 예외 처리
 */
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
    public UserInfoCommand getUserInfo(JoinUserCommand joinUserCommand) {

        return UserInfoClientResponse.toUserInfoCommandDto(userFeignClient.getUserInfo(joinUserCommand.toGetUserEmailPasswordRoleCommand())
                                                                          .getData());
    }

    @Override
    public void withdraw(PassportDto passportDto) {
        userFeignClient.withdraw(passportDto);
    }
}
