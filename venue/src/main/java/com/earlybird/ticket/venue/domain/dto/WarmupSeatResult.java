package com.earlybird.ticket.venue.domain.dto;

import com.earlybird.ticket.venue.domain.entity.constant.Grade;
import com.earlybird.ticket.venue.domain.entity.constant.Section;
import com.earlybird.ticket.venue.domain.entity.constant.Status;
import java.math.BigDecimal;
import java.util.UUID;

public record WarmupSeatResult(
    UUID seatId,
    UUID seatInstanceId,
    UUID concertId,
    UUID concertSequenceId,
    Section section,
    Integer row,
    Integer col,
    Integer floor,
    Grade grade,
    BigDecimal price,
    Status status
) {

}
