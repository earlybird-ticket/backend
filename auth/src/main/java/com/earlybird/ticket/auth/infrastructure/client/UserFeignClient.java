package com.earlybird.ticket.auth.infrastructure.client;

import com.earlybird.ticket.auth.application.dto.commander.CreateSellerCommand;
import com.earlybird.ticket.auth.application.dto.commander.CreateUserCommand;
import com.earlybird.ticket.auth.application.dto.commander.GetUserIdPasswordRoleCommand;
import com.earlybird.ticket.auth.infrastructure.dto.payload.UserInfoClientResponse;
import com.earlybird.ticket.common.entity.CommonDto;
import com.earlybird.ticket.common.entity.PassportDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "user-service", path = "/api/v1/internal/users")
public interface UserFeignClient {

    @PostMapping("/customer/sign-up")
    CommonDto<Void> createUser(@RequestBody CreateUserCommand createUserCommand);

    @PostMapping("/seller/sign-up")
    CommonDto<Void> createSeller(@RequestBody CreateSellerCommand createSellerCommand);

    @PostMapping
    CommonDto<UserInfoClientResponse> getUserInfo(
        GetUserIdPasswordRoleCommand getUserIdPasswordRoleCommand
    );

    @DeleteMapping
    void withdraw(PassportDto passportDto);
}
