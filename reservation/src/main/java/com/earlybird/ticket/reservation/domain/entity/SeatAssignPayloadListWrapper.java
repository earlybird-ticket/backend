package com.earlybird.ticket.reservation.domain.entity;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.reservation.domain.dto.request.SeatAssignPayload;

import java.util.List;

public class SeatAssignPayloadListWrapper implements EventPayload {
    private List<SeatAssignPayload> payloads;

    public SeatAssignPayloadListWrapper(List<SeatAssignPayload> payloads) {
        this.payloads = payloads;
    }

    public List<SeatAssignPayload> getPayloads() {
        return payloads;
    }
}