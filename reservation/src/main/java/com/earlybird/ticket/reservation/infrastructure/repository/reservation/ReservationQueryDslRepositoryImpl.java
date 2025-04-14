package com.earlybird.ticket.reservation.infrastructure.repository.reservation;

import com.earlybird.ticket.common.entity.PassportDto;
import com.earlybird.ticket.common.util.QueryUtil;
import com.earlybird.ticket.reservation.domain.dto.response.ReservationSearchResult;
import com.earlybird.ticket.reservation.domain.entity.QReservation;
import com.earlybird.ticket.reservation.domain.entity.QReservationSeat;
import com.earlybird.ticket.reservation.domain.entity.Reservation;
import com.earlybird.ticket.reservation.domain.entity.ReservationSeat;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import static com.earlybird.ticket.reservation.domain.entity.QReservation.reservation;

@Component
@RequiredArgsConstructor
public class ReservationQueryDslRepositoryImpl implements ReservationQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ReservationSearchResult> searchReservations(String q,
                                                            String startTime,
                                                            String endTime,
                                                            Pageable pageable,
                                                            PassportDto passportDto) {

        QReservation reservation = QReservation.reservation;
        QReservationSeat reservationSeat = QReservationSeat.reservationSeat;

        BooleanExpression predicate = reservation.userId.eq(passportDto.getUserId())
                                                        .and(likeSearchFields(q))
                                                        .and(betweenCreatedAt(startTime,
                                                                              endTime));

        List<Reservation> reservations = queryFactory.selectFrom(reservation)
                                                     .leftJoin(reservationSeat)
                                                     .on(reservationSeat.reservation.eq(reservation))
                                                     .fetchJoin()
                                                     .where(predicate)
                                                     .offset(pageable.getOffset())
                                                     .limit(pageable.getPageSize())
                                                     .orderBy(QueryUtil.getOrderSpecifier(pageable,
                                                                                          reservation))
                                                     .fetch();

        List<ReservationSearchResult> content = reservations.stream()
                                                            .map(r -> {
                                                                List<ReservationSeat> seats = queryFactory.selectFrom(reservationSeat)
                                                                                                          .where(reservationSeat.reservation.eq(r))
                                                                                                          .fetch();
                                                                return ReservationSearchResult.createFindReservationQuery(r,
                                                                                                                          seats);
                                                            })
                                                            .toList();

        Long total = queryFactory.select(reservation.count())
                                 .from(reservation)
                                 .where(predicate)
                                 .fetchOne();

        return new PageImpl<>(content,
                              pageable,
                              total);
    }

    private BooleanExpression likeSearchFields(String q) {
        if (q == null || q.isEmpty()) {
            return null;
        }
        return reservation.username.containsIgnoreCase(q)
                                   .or(reservation.concertName.containsIgnoreCase(q))
                                   .or(reservation.content.containsIgnoreCase(q));
    }

    private BooleanExpression betweenCreatedAt(String start,
                                               String end) {
        if (start.isBlank() || end.isBlank())
            return null;
        LocalDateTime startDate = LocalDateTime.parse(start);
        LocalDateTime endDate = LocalDateTime.parse(end);
        return reservation.createdAt.between(startDate,
                                             endDate);
    }
}
