package com.earlybird.ticket.venue.domain.entity.constant;

import com.earlybird.ticket.venue.common.exception.EnumTypeNotSupportException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Grade {
    GAZE("GAZE"),
    R("R"),
    S("S")
    ;
    private final String value;

    public static Grade getByValue(String value) {
        for (Grade grade : Grade.values()) {
            if (grade.getValue().equals(value)) {
                return grade;
            }
        }
        throw new EnumTypeNotSupportException();
    }
}
