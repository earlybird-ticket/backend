package com.earlybird.ticket.user.presentation;

import com.earlybird.ticket.common.entity.CommonDto;
import com.earlybird.ticket.user.application.service.UserService;
import com.earlybird.ticket.user.presentation.dto.request.FindUserEmailRequest;
import com.earlybird.ticket.user.presentation.dto.response.FindUserInfoResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/external/users")
@RequiredArgsConstructor
public class ExternalUserController {

    private final UserService userService;

    @PostMapping("/validate-email")
    public ResponseEntity<CommonDto<Void>> findUserEmailDuplicated(
        @RequestBody @Valid FindUserEmailRequest findUserEmailRequest
    ) {
        userService.findUserEmailDuplicated(findUserEmailRequest.toProcessUserEmailValidateCommand());
        CommonDto<Void> ok = CommonDto.ok(null, "사용가능한 이메일입니다.");
        return ResponseEntity.ok(ok);
    }

    @GetMapping
    public ResponseEntity<CommonDto<FindUserInfoResponse>> find(
        @RequestHeader(value = "X-User-Passport") String passport
    ) {
        FindUserInfoResponse findUserInfoResponse = FindUserInfoResponse.of(
            userService.findUser(passport)
        );

        return ResponseEntity.ok(CommonDto.ok(findUserInfoResponse, "사용자 정보 조회 성공"));
    }
}