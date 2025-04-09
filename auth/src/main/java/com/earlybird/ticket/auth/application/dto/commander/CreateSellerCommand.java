package com.earlybird.ticket.auth.application.dto.commander;

import com.earlybird.ticket.common.entity.constant.Role;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;

public record CreateSellerCommand(@NotNull(message = "email is Necessary") String email,
                                  @NotNull(message = "password is Necessary") String password,
                                  @NotNull(message = "name is Necessary") String name,
                                  @NotNull(message = "role is Necessary") Role role,
                                  @NotNull(message = "is Necessary") LocalDate birthDay,
                                  @Nullable String address,
                                  @Nullable String phone_number,
                                  @NotNull(message = "is Necessary") String business_number) {

    @Builder
    public CreateSellerCommand(@NotNull(message = "email is Necessary") String email,
                               @NotNull(message = "password is Necessary") String password,
                               @NotNull(message = "name is Necessary") String name,
                               @NotNull(message = "role is Necessary") Role role,
                               @NotNull(message = "is Necessary") LocalDate birthDay,
                               @Nullable String address,
                               @Nullable String phone_number,
                               @NotNull(message = "is Necessary") String business_number) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = Role.SELLER;
        this.birthDay = birthDay;
        this.address = address;
        this.phone_number = phone_number;
        this.business_number = business_number;
    }


}
