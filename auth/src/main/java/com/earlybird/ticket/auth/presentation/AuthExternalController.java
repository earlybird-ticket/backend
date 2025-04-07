package com.earlybird.ticket.auth.presentation;

import com.earlybird.ticket.auth.application.AuthService;
import com.earlybird.ticket.auth.application.dto.commander.CreateSellerCommand;
import com.earlybird.ticket.auth.application.dto.commander.CreateUserCommand;
import com.earlybird.ticket.auth.presentation.dto.request.CreateSellerRequest;
import com.earlybird.ticket.auth.presentation.dto.request.CreateUserRequest;
import com.earlybird.ticket.common.entity.CommonDto;
import com.earlybird.ticket.common.entity.constant.Code;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/external/auth")
public class AuthExternalController {
    private final AuthService authService;

    @PostMapping("customer/sign-up")
    public ResponseEntity<CommonDto<Void>> signUpCusotomer(@RequestBody @Valid CreateUserRequest createUserRequest) {
        //commander객체로 변환
        CreateUserCommand createUserCommand = CreateUserRequest.from(createUserRequest);

        authService.createUser(createUserCommand);

        CommonDto<Void> commonDto = CommonDto.<Void>builder()
                                             .status(HttpStatus.CREATED)
                                             .code(Code.CREATED)
                                             .message("생성 성공")
                                             .build();

        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(commonDto);
    }

    @PostMapping("seller/sign-up")
    public ResponseEntity<CommonDto<Void>> signUpSeller(@RequestBody @Valid CreateSellerRequest createUserRequest) {
        //commander객체로 변환
        CreateSellerCommand createSellerCommand = CreateSellerRequest.from(createUserRequest);

        authService.createSeller(createSellerCommand);

        CommonDto<Void> commonDto = CommonDto.<Void>builder()
                                             .status(HttpStatus.CREATED)
                                             .code(Code.CREATED)
                                             .message("생성 성공")
                                             .build();

        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(commonDto);
    }


    //TODO:: SignIn
    // ""
    // 토큰 반환


}
