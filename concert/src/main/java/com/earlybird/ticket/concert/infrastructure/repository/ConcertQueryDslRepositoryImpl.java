package com.earlybird.ticket.concert.infrastructure.repository;

import static com.earlybird.ticket.concert.domain.QConcert.concert;

import com.earlybird.ticket.concert.domain.Concert;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConcertQueryDslRepositoryImpl implements ConcertQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Concert> search(PageRequest pageRequest, String sort, String q, String orderBy) {
        BooleanBuilder builder = new BooleanBuilder();

        if (q != null && !q.isEmpty()) {
            builder.and(concert.concertName.contains(q));
        }

        Order order = sort.equals("asc") ? Order.ASC : Order.DESC;

        OrderSpecifier<?> orderSpecifier = new OrderSpecifier<>(order, concert.concertName);

        List<Concert> result = jpaQueryFactory
                .selectFrom(concert)
                .where(builder)
                .orderBy(orderSpecifier)
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .fetch();

        long total = jpaQueryFactory
                .selectFrom(concert)
                .where(builder)
                .fetch().size();

        return new PageImpl<>(result, pageRequest, total);
    }
}
