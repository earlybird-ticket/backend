package com.earlybird.ticket.venue.infrastructure.repository;

import static com.earlybird.ticket.venue.domain.entity.QSeat.seat;
import static com.earlybird.ticket.venue.domain.entity.QSeatInstance.seatInstance;

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
                                seatInstance.count(),
                                seat.floor,
                                seatInstance.grade,
                                seatInstance.price
                                )
                        )
                        .from(seat)
                        .leftJoin(seat.seatInstances, seatInstance)
                        .where(
                            seatInstance.concertSequenceId.eq(concertSequenceId),
                            seatInstance.status.eq(Status.FREE),
                            seatInstance.deletedAt.isNull(),
                            seat.deletedAt.isNull()
                        )
                        .groupBy(
                            seat.section,
                            seat.floor,
                            seatInstance.grade,
                            seatInstance.price
                        )
                        .fetch();

        UUID concertId =
                queryFactory
                        .select(
                        seatInstance.concertId
                        )
                        .from(seatInstance)
                        .where(
                                seatInstance.concertSequenceId.eq(concertSequenceId),
                                seatInstance.deletedAt.isNull()
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
                                seatInstance.id,
                                seat.row,
                                seat.col,
                                seatInstance.status,
                                seatInstance.price

                        ))
                        .from(seat)
                        .leftJoin(seat.seatInstances, seatInstance)
                        .where(
                                seatInstance.concertSequenceId.eq(concertSequenceId),
                                seat.section.eq(section),
                                seatInstance.deletedAt.isNull(),
                                seat.deletedAt.isNull()
                        )
                        .fetch();

        SeatListResult seatListResult =
                queryFactory
                        .select(Projections.constructor(
                                SeatListResult.class,
                                seatInstance.concertId,
                                seatInstance.concertSequenceId,
                                seat.section,
                                seatInstance.grade,
                                seat.floor,
                                Expressions.constant(new ArrayList<>())

                        ))
                        .from(seat)
                        .leftJoin(seat.seatInstances, seatInstance)
                        .where(
                                seatInstance.concertSequenceId.eq(concertSequenceId),
                                seat.section.eq(section),
                                seatInstance.deletedAt.isNull(),
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
                .join(seat.seatInstances, seatInstance).fetchJoin()
                .where(
                        seatInstance.id.in(seatInstanceIdList),
                        seatInstance.deletedAt.isNull(),
                        seat.deletedAt.isNull()
                        )
                .fetch();
    }

    @Override
    public Seat findSeatBySeatInstanceId(UUID seatInstanceId) {
        return queryFactory
                .selectDistinct(seat)
                .from(seat)
                .join(seat.seatInstances, seatInstance).fetchJoin()
                .where(
                        seatInstance.id.eq(seatInstanceId),
                        seatInstance.deletedAt.isNull(),
                        seat.deletedAt.isNull()
                )
                .fetchOne();
    }
}
