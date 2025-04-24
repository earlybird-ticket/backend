package com.earlybird.ticket.venue.common.dto;

import java.util.List;
import java.util.Map;

public record RedisReadResult<T>(
        List<T> results,
        Map<String, String> rawMap
) {
}
