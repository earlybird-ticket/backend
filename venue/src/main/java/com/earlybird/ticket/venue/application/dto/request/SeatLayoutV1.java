package com.earlybird.ticket.venue.application.dto.request;

import com.earlybird.ticket.venue.domain.dto.WarmupSeatResult;
import com.earlybird.ticket.venue.domain.entity.constant.Section;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.Builder;

public record SeatLayoutV1(
    String schemaVersion,
    UUID concertId,
    UUID concertSequenceId,
    Section section,
    List<SeatLayoutItem> seats
) {

    public SeatLayoutV1 {
        seats = List.copyOf(seats);
    }

    @Builder
    public SeatLayoutV1(
        UUID concertId, UUID concertSequenceId, Section section, List<SeatLayoutItem> seats) {
        this("seat-layout-v1", concertId, concertSequenceId, section, seats);
    }

    @Builder
    public record SeatLayoutItem(
        UUID seatInstanceId,
        int row,
        int col,
        int floor,
        BigDecimal price
    ) {

        public SeatLayoutItem {
            price = Objects.requireNonNull(price).setScale(2, RoundingMode.UNNECESSARY);
        }


        public static SeatLayoutItem toSeatLayoutItem(WarmupSeatResult warmupSeatResult) {
            return SeatLayoutItem.builder()
                .seatInstanceId(warmupSeatResult.seatInstanceId())
                .row(warmupSeatResult.row())
                .col(warmupSeatResult.col())
                .floor(warmupSeatResult.floor())
                .price(warmupSeatResult.price())
                .build();

        }

    }

}
