package com.earlybird.ticket.common.entity.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum Role {

    USER("USER"), MASTER("MASTER"), SELLER("SELLER");

    private final String value;

    public static Role from(String role) {
        return Arrays.stream(Role.values())
                     .filter(r -> r.value.equals(role))
                     .findFirst()
                     .orElseThrow(() -> new RuntimeException("Invalid role : " + role));
    }
}
