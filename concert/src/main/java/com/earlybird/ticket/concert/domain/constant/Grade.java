package com.earlybird.ticket.concert.domain.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Grade {
    GAZE("GAZE"),
    R("R"),
    S("S");
    private final String value;

    public static Grade getByValue(String value) {
        for (Grade grade : Grade.values()) {
            if (grade.getValue().equals(value)) {
                return grade;
            }
        }
        throw new IllegalArgumentException();
    }
}
