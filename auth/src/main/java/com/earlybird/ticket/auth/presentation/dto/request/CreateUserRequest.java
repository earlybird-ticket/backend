package com.earlybird.ticket.auth.presentation.dto.request;

import com.earlybird.ticket.auth.application.dto.commander.CreateUserCommand;
import com.earlybird.ticket.common.entity.constant.Role;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

public record CreateUserRequest(@NotNull(message = "email is necessary") String email,
                                @NotNull(message = "password is necessary") String password,
                                @NotNull(message = "name is necessary") String name,
                                Role role,
                                @NotNull(message = "birthDay is necessary") LocalDate birthDay,
                                @Nullable String address,
                                @Nullable String phone_number) {

    @Builder
    public CreateUserRequest(@NotNull(message = "email is necessary") String email,
                             @NotNull(message = "password is necessary") String password,
                             @NotNull(message = "name is necessary") String name,
                             Role role,
                             @NotNull(message = "birthDay is necessary") LocalDate birthDay,
                             @Nullable String address,
                             @Nullable String phone_number) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
        this.birthDay = birthDay;
        this.address = address;
        this.phone_number = phone_number;
    }

    public static CreateUserCommand from(CreateUserRequest createUserRequest) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return CreateUserCommand.builder()
                                .email(createUserRequest.email())
                                .password(passwordEncoder.encode(createUserRequest.password()))
                                .name(createUserRequest.name())
                                .role(Role.USER)
                                .birthDay(createUserRequest.birthDay())
                                .address(createUserRequest.address())
                                .phone_number(createUserRequest.phone_number())
                                .build();
    }

}
