package com.earlybird.ticket.admin.infrastructure.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminQueryDslRepositoryImpl implements AdminQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;


}
