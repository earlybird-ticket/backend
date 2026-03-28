package com.earlybird.ticket.venue.application.dto.request;

import java.time.LocalDateTime;
import java.util.UUID;

public record WarmupSeatCommand(
    UUID concertSequenceId,
    LocalDateTime ticketExpiredAt,
    LocalDateTime vipTicketExpiredAt
) {
}
