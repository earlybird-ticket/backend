package com.earlybird.ticket.auth.presentation.dto.request;

import com.earlybird.ticket.auth.application.dto.commander.CreateSellerCommand;
import com.earlybird.ticket.common.entity.constant.Role;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

public record CreateSellerRequest(@NotNull(message = "email is Necessary") String email,
                                  @NotNull(message = "password is Necessary") String password,
                                  @NotNull(message = "name is Necessary") String name,
                                  Role role,
                                  @NotNull(message = "email is Necessary") LocalDate birthday,
                                  @Nullable String address,
                                  @Nullable String phone_number,
                                  @NotNull(message = "email is Necessary") String business_number) {

    @Builder
    public CreateSellerRequest(@NotNull(message = "email is Necessary") String email,
                               @NotNull(message = "password is Necessary") String password,
                               @NotNull(message = "name is Necessary") String name,
                               Role role,
                               @NotNull(message = "birthday is Necessary") LocalDate birthday,
                               @Nullable String address,
                               @Nullable String phone_number,
                               @NotNull(message = "businessNUmber is Necessary") String business_number) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
        this.birthday = birthday;
        this.address = address;
        this.phone_number = phone_number;
        this.business_number = business_number;
    }

    public static CreateSellerCommand from(CreateSellerRequest createSellerRequest) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return CreateSellerCommand.builder()
                                  .email(createSellerRequest.email())
                                  .password(passwordEncoder.encode(createSellerRequest.password()))
                                  .name(createSellerRequest.name())
                                  .role(Role.SELLER)
                                  .birthday(createSellerRequest.birthday())
                                  .address(createSellerRequest.address())
                                  .phone_number(createSellerRequest.phone_number())
                                  .business_number(createSellerRequest.business_number())
                                  .build();
    }

}
