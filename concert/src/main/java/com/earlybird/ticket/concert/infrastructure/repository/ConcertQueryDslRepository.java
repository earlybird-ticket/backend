package com.earlybird.ticket.concert.infrastructure.repository;

import com.earlybird.ticket.concert.domain.Concert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface ConcertQueryDslRepository {

    Page<Concert> search(PageRequest pageRequest, String sort, String q, String orderBy);
}
