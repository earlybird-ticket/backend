package com.earlybird.ticket.reservation.domain.dto.response;

import com.earlybird.ticket.common.entity.EventPayload;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TestPayload implements EventPayload {
    private String test;
}
