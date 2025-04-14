package com.earlybird.ticket.concert.infrastructure.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConcertQueryDslRepositoryImpl implements ConcertQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

}
