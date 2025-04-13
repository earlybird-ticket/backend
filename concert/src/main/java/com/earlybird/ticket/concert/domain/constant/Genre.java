package com.earlybird.ticket.concert.domain.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Genre {
    ROCK("ROCK"),
    POP("POP"),
    JPOP("JPOP"),
    KPOP("KPOP"),
    CLASSICAL("CLASSICAL"),
    COUNTRY("COUNTRY"),
    BLUES("BLUES");

    private final String genre;
}
