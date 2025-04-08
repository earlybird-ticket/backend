package com.earlybird.ticket.venue.infrastructure.repository;

import static com.earlybird.ticket.venue.domain.entity.QSeat.seat;
import static com.earlybird.ticket.venue.domain.entity.QSeatInstant.seatInstant;

import com.earlybird.ticket.venue.domain.entity.constant.Status;
import com.earlybird.ticket.venue.domain.dto.SectionListResult;
import com.earlybird.ticket.venue.domain.dto.SectionListResult.SectionResult;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class SeatQueryRepositoryImpl implements SeatQueryRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public SectionListResult findSectionList(UUID concertSequenceId) {

        List<SectionResult> sectionQueryDslList =
                queryFactory
                        .select(
                        Projections.constructor(
                                SectionResult.class,
                                seat.section,
                                seatInstant.count(),
                                seat.floor,
                                seatInstant.grade,
                                seatInstant.price
                                )
                        )
                        .from(seat)
                        .leftJoin(seat).on(seat.eq(seatInstant.seat))
                        .where(
                            seatInstant.concertSequenceId.eq(concertSequenceId),
                            seatInstant.status.eq(Status.FREE),
                            seatInstant.deletedAt.isNull(),
                            seat.deletedAt.isNull()
                        )
                        .groupBy(
                            seat.section,
                            seat.floor,
                            seatInstant.grade,
                            seatInstant.price
                        )
                        .fetch();

        UUID concertId =
                queryFactory
                        .select(
                        seatInstant.concertId
                        )
                        .from(seatInstant)
                        .where(
                                seatInstant.concertSequenceId.eq(concertSequenceId),
                                seatInstant.deletedAt.isNull()
                        )
                        .fetchOne();

        return SectionListResult.builder()
                .concertId(concertId)
                .concertSequenceId(concertSequenceId)
                .sectionList(sectionQueryDslList)
                .build();
    }
}
