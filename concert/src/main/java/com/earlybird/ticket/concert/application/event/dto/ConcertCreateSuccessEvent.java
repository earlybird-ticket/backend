package com.earlybird.ticket.concert.application.event.dto;

import com.earlybird.ticket.common.entity.EventPayload;
import com.earlybird.ticket.common.entity.PassportDto;
import com.earlybird.ticket.concert.domain.Concert;
import com.earlybird.ticket.concert.domain.constant.Grade;
import com.earlybird.ticket.concert.domain.constant.Section;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ConcertCreateSuccessEvent(
        PassportDto passportDto,
        UUID concertId,
        UUID venueId,
        UUID hallId,
        List<UUID> concertSequenceList,
        List<SeatInstanceInfo> seatInstanceInfoList
) implements EventPayload {

    public static ConcertCreateSuccessEvent toPayload(PassportDto passportDto, Concert concert) {
        return ConcertCreateSuccessEvent.builder()
                .passportDto(passportDto)
                .seatInstanceInfoList(
                        concert.getSeatInstanceInfo()
                                .stream()
                                .map(info -> SeatInstanceInfo.builder()
                                        .section(info.getSection())
                                        .grade(info.getGrade())
                                        .price(info.getPrice())
                                        .build()
                                )
                                .toList()
                )
                .build();
    }

    @Builder
    public record SeatInstanceInfo(
            Section section,
            Grade grade,
            Integer price
    ) {

    }
}
