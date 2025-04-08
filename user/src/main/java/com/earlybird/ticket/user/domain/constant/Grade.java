package com.earlybird.ticket.user.domain.constant;

import java.util.Arrays;
import java.util.NoSuchElementException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Grade {
    MVP("MVP"),
    DIAMOND("DIAMOND"),
    PLATINUM("PLATINUM"),
    GOLD("GOLD"),
    SILVER("SILVER"),
    BRONZE("BRONZE");

    private final String value;

    public static Grade from(String grade) {
        return Arrays.stream(Grade.values())
            .filter(g -> g.value.equals(grade))
            .findFirst()
            .orElseThrow(() -> new NoSuchElementException("그런 권한은 없습니다."));
    }
}
