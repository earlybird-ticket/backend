package com.earlybird.ticket.auth.application.dto.commander;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

public record JoinUserCommand(@NotNull(message = "email is Necessary") String email,
                              @NotNull(message = "password is Necessary") String password) {


    @Builder
    public JoinUserCommand(@NotNull(message = "email is Necessary") String email,
                           @NotNull(message = "password is Necessary") String password) {
        this.email = email;
        this.password = password;
    }

    public GetUserIdPasswordRoleCommand toGetUserEmailPasswordRoleCommand() {
        return GetUserIdPasswordRoleCommand.builder()
            .email(email)
            .build();
    }

}
