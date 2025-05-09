package com.earlybird.ticket.user.presentation;

import com.earlybird.ticket.common.entity.CommonDto;
import com.earlybird.ticket.user.application.service.UserService;
import com.earlybird.ticket.user.presentation.dto.request.FindUserEmailRequest;
import com.earlybird.ticket.user.presentation.dto.request.SignUpUserCustomerRequest;
import com.earlybird.ticket.user.presentation.dto.request.SignUpUserSellerRequest;
import com.earlybird.ticket.user.presentation.dto.request.UpdateUserCustomerPasswordRequest;
import com.earlybird.ticket.user.presentation.dto.response.GetUserEmailPasswordRoleResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/internal/users")
@RequiredArgsConstructor
public class InternalUserController {

    private final UserService userService;

    @PostMapping("/seller/sign-up")
    public ResponseEntity<CommonDto<Void>> signUpSeller(
        @Valid @RequestBody SignUpUserSellerRequest sellerRequest
    ) {
        userService.createUserSeller(sellerRequest.createUserSellerCommand());
        return ResponseEntity.ok(CommonDto.ok(null, "회원가입 성공"));
    }

    @PostMapping("/customer/sign-up")
    public ResponseEntity<CommonDto<Void>> signUpCustomer(
        @Valid @RequestBody SignUpUserCustomerRequest customerRequest
    ) {
        userService.createUserCustomer(customerRequest.createUserCustomerCommand());
        return ResponseEntity.ok(CommonDto.ok(null, "회원가입 성공"));
    }

    @PostMapping
    public ResponseEntity<CommonDto<GetUserEmailPasswordRoleResponse>> findUserLoginInfo(
        @Valid @RequestBody FindUserEmailRequest findUserEmailRequest
    ) {
        GetUserEmailPasswordRoleResponse userInfoResponse = GetUserEmailPasswordRoleResponse.of(
            userService.findUserByEmail(findUserEmailRequest.email())
        );
        return ResponseEntity.ok(CommonDto.ok(userInfoResponse, "사용자 정보 조회 성공"));
    }

    @DeleteMapping
    public ResponseEntity<CommonDto<Void>> deleteUser(
        @RequestHeader(name = "X-User-Passport") String passport
    ) {
        userService.deleteUser(passport);
        return ResponseEntity.ok(CommonDto.ok(null, "사용자 삭제 완료"));
    }

    @PatchMapping("/change-password")
    public ResponseEntity<CommonDto<Void>> changeUserCustomerPassword(
        @RequestHeader(value = "X-User-Passport") String passport,
        @Valid @RequestBody UpdateUserCustomerPasswordRequest passwordRequest
    ) {
        userService.updateUserCustomerPassword(
            passport,
            passwordRequest.toUpdateCustomerPasswordCommand()
        );

        return ResponseEntity.ok(CommonDto.ok(null, "비밀번호 변경 성공"));
    }
}
