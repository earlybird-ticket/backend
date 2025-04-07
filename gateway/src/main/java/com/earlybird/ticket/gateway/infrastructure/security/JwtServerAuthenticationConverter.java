package com.earlybird.ticket.gateway.infrastructure.security;

import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j(topic = "JWT 인증 객체 변환")
@Component
public class JwtServerAuthenticationConverter implements ServerAuthenticationConverter {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return Mono.justOrEmpty(exchange)
            .doFirst(() -> log.info("헤더 추출"))
            .flatMap(ex -> Mono.justOrEmpty(
                ex.getRequest().getHeaders().getFirst(AUTHORIZATION_HEADER)))
            .filter(Objects::nonNull)
            // 헤더에서 토큰 추출 후 임시로 Authenticatin 생성 및 ReactiveAuthenticationManager로 넘김
            .doFirst(() -> log.info("임시 인증 토큰 생성"))
            .map(auth -> new UsernamePasswordAuthenticationToken(auth, null));
    }
}