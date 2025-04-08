package com.earlybird.ticket.auth.presentation.dto.request;


import com.earlybird.ticket.auth.application.dto.commander.JoinUserCommand;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

public record JoinUserRequest(@NotNull(message = "email is Necessary") String email,
                              @NotNull(message = "password is Necessary") String password) {

    @Builder
    public JoinUserRequest(String email,
                           String password) {
        this.email = email;
        this.password = password;
    }

    public static JoinUserCommand from(@Valid JoinUserRequest joinUserRequest) {
        return JoinUserCommand.builder()
                              .email(joinUserRequest.email)
                              .password(joinUserRequest.password)
                              .build();
    }
}
