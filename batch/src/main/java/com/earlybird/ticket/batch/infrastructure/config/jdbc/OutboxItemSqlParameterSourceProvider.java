package com.earlybird.ticket.batch.infrastructure.config.jdbc;

import com.earlybird.ticket.batch.domain.entity.Outbox;
import org.springframework.batch.item.database.ItemSqlParameterSourceProvider;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;


public class OutboxItemSqlParameterSourceProvider implements
    ItemSqlParameterSourceProvider<Outbox> {

    @Override
    public SqlParameterSource createSqlParameterSource(Outbox item) {
        return new BeanPropertySqlParameterSource(item);
    }
}
