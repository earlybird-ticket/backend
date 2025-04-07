package com.earlybird.ticket.auth.application.dto.commander;

import com.earlybird.ticket.common.entity.constant.Role;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;

public record CreateUserCommand(@NotNull(message = "email is Necessary") String email,
                                @NotNull(message = "password is Necessary") String password,
                                @NotNull(message = "name is Necessary") String name,
                                @NotNull(message = "role is Necessary") Role role,
                                @NotNull(message = "birthday is Necessary") LocalDate birthday,
                                @Nullable String address,
                                @Nullable String phone_number) {

    @Builder
    public CreateUserCommand(@NotNull(message = "email is Necessary") String email,
                             @NotNull(message = "password is Necessary") String password,
                             @NotNull(message = "name is Necessary") String name,
                             @NotNull(message = "role is Necessary") Role role,
                             @NotNull(message = "birthday is Necessary") LocalDate birthday,
                             @Nullable String address,
                             @Nullable String phone_number) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
        this.birthday = birthday;
        this.address = address;
        this.phone_number = phone_number;
    }
}
