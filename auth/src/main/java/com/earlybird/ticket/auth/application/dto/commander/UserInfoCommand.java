package com.earlybird.ticket.auth.application.dto.commander;

import com.earlybird.ticket.common.entity.constant.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

public record UserInfoCommand(@NotNull(message = "userId is Necessary") Long userId,
                              @NotNull(message = "password is Necessary") String password,
                              @NotNull(message = "role is Necessary") Role role) {

    @Builder
    public UserInfoCommand(@NotNull(message = "userId is Necessary") Long userId,
                           @NotNull(message = "password is Necessary") String password,
                           @NotNull(message = "role is Necessary") Role role) {
        this.userId = userId;
        this.password = password;
        this.role = role;
    }
}
