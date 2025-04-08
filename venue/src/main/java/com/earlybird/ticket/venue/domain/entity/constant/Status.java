package com.earlybird.ticket.venue.domain.entity.constant;

import com.earlybird.ticket.venue.common.exception.EnumTypeNotSupportException;
import jakarta.ws.rs.NotSupportedException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Status {
    PREEMPTED("PREEMPTED"),
    CONFIRMED("CONFIRMED"),
    FREE("FREE"),
    RESERVED("RESERVED")
    ;
    private final String value;

    public static Status getByValue(String value) {
        for (Status status : Status.values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        throw new EnumTypeNotSupportException();
    }
}
