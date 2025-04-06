package com.earlybird.ticket.auth.presentation;

import com.earlybird.ticket.auth.application.AuthService;
import com.earlybird.ticket.common.entity.CommonDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/external/auth")
public class AuthExternalController {
    private final AuthService authService;

    //TODO:: SignUp
    // RequestDto는 commander로 변환해서 Application에서 전달
    // 응답 Void
    @PostMapping("sign-up")
    public ResponseEntity<CommonDto<Void>> signUp(
    ){
        return null;
    }


    //TODO:: SignIn
    // ""
    // 토큰 반환


}
