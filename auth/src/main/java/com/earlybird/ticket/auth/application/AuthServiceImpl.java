package com.earlybird.ticket.auth.application;

import com.earlybird.ticket.auth.application.dto.commander.CreateSellerCommand;
import com.earlybird.ticket.auth.application.dto.commander.CreateUserCommand;
import com.earlybird.ticket.auth.application.dto.commander.JoinUserCommand;
import com.earlybird.ticket.auth.application.dto.commander.UserInfoCommand;
import com.earlybird.ticket.auth.common.exception.AccountMismatchException;
import com.earlybird.ticket.auth.common.exception.UserNotFoundException;
import com.earlybird.ticket.common.entity.constant.Role;
import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserClient userClient;
    private final TokenProvider tokenProvider;

    private static boolean validatePassword(JoinUserCommand joinUserCommand,
                                            PasswordEncoder passwordEncoder,
                                            @Validated UserInfoCommand userInfo) {
        return passwordEncoder.matches(joinUserCommand.password(),
                                       userInfo.password());
    }

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

    @Override
    public String join(JoinUserCommand joinUserCommand) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        UserInfoCommand userInfo = null;
        try {
            //회원정보 가져옴(고유번호, 비밀번호, 권한)
            //            userInfo = userClient.getUserInfo(joinUserCommand);
            userInfo = UserInfoCommand.builder()
                                      .userId(1L)
                                      .role(Role.USER)
                                      .password(passwordEncoder.encode("1234"))
                                      .build();
        } catch (FeignException.NotFound e) {
            throw new UserNotFoundException();
        } catch (RetryableException e) {
            throw new UserNotFoundException();
        } catch (FeignException e) {
            log.error(e.getMessage(),
                      e);
        }

        //비밀번호 매칭 여부 검사
        boolean flag = validatePassword(joinUserCommand,
                                        passwordEncoder,
                                        userInfo);
        //고유번호
        Long userId = userInfo.userId();
        //권한
        String role = userInfo.role()
                              .getValue();

        //정상이면 토큰 생성
        if (flag) {
            return tokenProvider.generateAccessToken(userId,
                                                     role);
        }
        throw new AccountMismatchException();

    }


}
