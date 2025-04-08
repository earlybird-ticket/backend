package com.earlybird.ticket.auth.infrastructure.dto.payload;


import com.earlybird.ticket.auth.application.dto.commander.UserInfoCommand;
import com.earlybird.ticket.common.entity.constant.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

public record UserInfoClientResponse(@NotNull(message = "userId is Necessary") Long userId,
                                     @NotNull(message = "password is Necessary") String password,
                                     @NotNull(message = "role is Necessary") Role role) {

    @Builder
    public UserInfoClientResponse(@NotNull(message = "userId is Necessary") Long userId,
                                  @NotNull(message = "password is Necessary") String password,
                                  @NotNull(message = "role is Necessary") Role role) {
        this.userId = userId;
        this.password = password;
        this.role = role;
    }

    public static UserInfoCommand toUserInfoCommandDto(UserInfoClientResponse data) {
        return UserInfoCommand.builder()
                              .userId(data.userId)
                              .password(data.password)
                              .role(data.role)
                              .build();
    }
}
