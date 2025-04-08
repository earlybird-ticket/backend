package com.earlybird.ticket.auth.infrastructure.client;

import com.earlybird.ticket.auth.application.dto.commander.CreateSellerCommand;
import com.earlybird.ticket.auth.application.dto.commander.CreateUserCommand;
import com.earlybird.ticket.auth.application.dto.commander.JoinUserCommand;
import com.earlybird.ticket.auth.infrastructure.dto.payload.UserInfoClientResponse;
import com.earlybird.ticket.common.entity.CommonDto;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service", url = "api/v1/internal/users")
public interface UserFeignClient {

    @PostMapping("/customer/sign-up")
    void createUser(@RequestBody @Valid CreateUserCommand createUserCommand);

    @PostMapping("/seller/sign-up")
    void createSeller(@RequestBody @Valid CreateSellerCommand createSellerCommand);

    @GetMapping
    CommonDto<UserInfoClientResponse> getUserInfo(JoinUserCommand joinUserCommand);
}
