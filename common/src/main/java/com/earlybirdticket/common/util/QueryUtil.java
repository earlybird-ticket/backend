package com.earlybirdticket.common.util;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

public class QueryUtil {

    public static OrderSpecifier<?> getOrderSpecifier(Pageable pageable, Path<?> path) {
        Sort.Order order = pageable.getSort()
                .stream()
                .findFirst()
                .orElse(Sort.Order.by("createdAt").with(Direction.DESC));

        Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
        Path<?> fieldPath = Expressions.path(path.getType(), path, order.getProperty());

        return new OrderSpecifier(direction, fieldPath);
    }
}
