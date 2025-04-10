package com.earlybird.ticket.venue.infrastructure.repository;

import static com.earlybird.ticket.venue.domain.entity.QSeat.seat;
import static com.earlybird.ticket.venue.domain.entity.QSeatInstant.seatInstant;

import com.earlybird.ticket.venue.domain.dto.SeatListResult;
import com.earlybird.ticket.venue.domain.dto.SeatListResult.SeatResult;
import com.earlybird.ticket.venue.domain.entity.Seat;
import com.earlybird.ticket.venue.domain.entity.constant.Section;
import com.earlybird.ticket.venue.domain.entity.constant.Status;
import com.earlybird.ticket.venue.domain.dto.SectionListResult;
import com.earlybird.ticket.venue.domain.dto.SectionListResult.SectionResult;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
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
                        .leftJoin(seat.seatInstants, seatInstant)
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

    @Override
    public SeatListResult findSeatList(UUID concertSequenceId, Section section) {

        List<SeatResult> seatResultList =
                queryFactory
                        .select(Projections.constructor(
                                SeatResult.class,
                                seatInstant.id,
                                seat.row,
                                seat.col,
                                seatInstant.status,
                                seatInstant.price

                        ))
                        .from(seat)
                        .leftJoin(seat.seatInstants, seatInstant)
                        .where(
                                seatInstant.concertSequenceId.eq(concertSequenceId),
                                seat.section.eq(section),
                                seatInstant.deletedAt.isNull(),
                                seat.deletedAt.isNull()
                        )
                        .fetch();

        SeatListResult seatListResult =
                queryFactory
                        .select(Projections.constructor(
                                SeatListResult.class,
                                seatInstant.concertId,
                                seatInstant.concertSequenceId,
                                seat.section,
                                seatInstant.grade,
                                seat.floor,
                                Expressions.constant(new ArrayList<>())

                        ))
                        .from(seat)
                        .leftJoin(seat.seatInstants, seatInstant)
                        .where(
                                seatInstant.concertSequenceId.eq(concertSequenceId),
                                seat.section.eq(section),
                                seatInstant.deletedAt.isNull(),
                                seat.deletedAt.isNull()
                        )
                        .fetchOne();

        return SeatListResult.copyWithSeatList(seatListResult, seatResultList);
    }

    @Override
    public List<Seat> findSeatWithSeatInstance(List<UUID> seatInstanceIdList) {
        return queryFactory
                .selectDistinct(seat)
                .from(seat)
                .join(seat.seatInstants, seatInstant).fetchJoin()
                .where(
                        seatInstant.id.in(seatInstanceIdList),
                        seatInstant.deletedAt.isNull(),
                        seat.deletedAt.isNull()
                        )
                .fetch();
    }
}
