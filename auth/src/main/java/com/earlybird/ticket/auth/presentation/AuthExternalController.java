package com.earlybird.ticket.auth.presentation;

import com.earlybird.ticket.auth.application.AuthService;
import com.earlybird.ticket.auth.application.dto.commander.CreateSellerCommand;
import com.earlybird.ticket.auth.application.dto.commander.CreateUserCommand;
import com.earlybird.ticket.auth.application.dto.commander.JoinUserCommand;
import com.earlybird.ticket.auth.presentation.dto.request.CreateSellerRequest;
import com.earlybird.ticket.auth.presentation.dto.request.CreateUserRequest;
import com.earlybird.ticket.auth.presentation.dto.request.JoinUserRequest;
import com.earlybird.ticket.common.entity.CommonDto;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/external/auth")
@Slf4j
public class AuthExternalController {
    private final AuthService authService;

    @PostMapping("customer/sign-up")
    public ResponseEntity<CommonDto<Void>> signUpCustomer(@RequestBody @Valid CreateUserRequest createUserRequest) {
        //commander객체로 변환
        CreateUserCommand createUserCommand = CreateUserRequest.from(createUserRequest);

        authService.createUser(createUserCommand);

        CommonDto<Void> commonDto = CommonDto.created(null,
                                                      "유저 생성 성공");

        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(commonDto);
    }

    @PostMapping("seller/sign-up")
    public ResponseEntity<CommonDto<Void>> signUpSeller(@RequestBody @Valid CreateSellerRequest createUserRequest) {
        //commander객체로 변환
        CreateSellerCommand createSellerCommand = CreateSellerRequest.from(createUserRequest);

        authService.createSeller(createSellerCommand);

        CommonDto<Void> commonDto = CommonDto.ok(null,
                                                 "판매자 생성 성공");

        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(commonDto);
    }


    @PostMapping("/sign-in")
    public ResponseEntity<CommonDto<String>> signIn(@RequestBody @Valid JoinUserRequest joinUserRequest,
                                                    HttpServletResponse response) {
        //commander 객체로 변환
        JoinUserCommand joinUserCommand = JoinUserRequest.from(joinUserRequest);


        response.setHeader("Authorization",
                           authService.join(joinUserCommand));

        log.info("성공");

        return ResponseEntity.status(HttpStatus.OK)
                             .body(CommonDto.ok(null,
                                                "회원가입 성공"));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<CommonDto<String>> withdraw(@RequestHeader("X-Role-User") String passport) {

        authService.withdraw(passport);

        return ResponseEntity.status(HttpStatus.ACCEPTED)
                             .body(CommonDto.accepted(null,
                                                      "회원 탈퇴 성공"));
    }


}
