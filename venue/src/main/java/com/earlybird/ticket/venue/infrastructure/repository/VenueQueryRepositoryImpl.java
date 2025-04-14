package com.earlybird.ticket.venue.infrastructure.repository;

import com.earlybird.ticket.venue.domain.entity.Venue;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

import static com.earlybird.ticket.venue.domain.entity.QHall.hall;
import static com.earlybird.ticket.venue.domain.entity.QVenue.venue;

@Repository
@RequiredArgsConstructor
public class VenueQueryRepositoryImpl implements VenueQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Venue findVenueWithHallById(UUID venueId) {
        return queryFactory
                .selectDistinct(venue)
                .from(venue)
                .join(venue.halls, hall).fetchJoin()
                .where(
                        venue.id.eq(venueId),
                        venue.deletedAt.isNull(),
                        hall.deletedAt.isNull()
                )
                .fetchOne();
    }
}
