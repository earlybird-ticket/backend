package com.earlybird.ticket.gateway.infrastructure.security.filter;

import com.earlybird.ticket.common.entity.PassportDto;
import com.earlybird.ticket.common.entity.constant.Role;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j(topic = "권한별 인가")
@Component
public class AddPassportHeaderGatewayFilter implements GlobalFilter, Ordered {

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    // TODO: 정규식 패턴으로 변경
    private final String ALLOWED_UNAUTHORIZED_REQUEST = "^/api/v1/auth(/.*)?$";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        // TODO: JWT 토큰 발급 이후 검증
        String path = exchange.getRequest().getURI().getPath();
        log.info("path = {}", path);
//         회원가입과 로그인 과정은 인증되지 않은 상태이므로 인가 필터 거치지 X
        if (Pattern.matches(ALLOWED_UNAUTHORIZED_REQUEST, path)) {
            return chain.filter(exchange)
                .doFirst(() -> log.info("no auth request path = {}", path))
                .then(Mono.fromRunnable(() -> {
                    ServerHttpResponse response = exchange.getResponse();
                    log.info("no auth response status code = {}", response.getStatusCode());
                }));
        }

        return getAuthentication()
            .doFirst(() -> log.info("auth request path = {}", path))
            .flatMap(auth -> {
                Long userId = (Long) auth.getPrincipal();
                Role userRole = auth.getAuthorities().stream()
                    .map(role -> Role.from(
                        role.getAuthority().substring("ROLE_".length())))
                    .toList()
                    .get(0);

                PassportDto PassportDto = com.earlybird.ticket.common.entity.PassportDto.builder()
                    .userId(userId)
                    .userRole(String.valueOf(userRole))
                    .build();

                try {
                    ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                        .header("X-User-Passport",
                            objectMapper.writeValueAsString(PassportDto))
                        .build();
                    return chain.filter(exchange.mutate().request(modifiedRequest).build());
                } catch (JsonProcessingException e) {
                    return Mono.error(e);
                }

            });

    }

    private Mono<UsernamePasswordAuthenticationToken> getAuthentication() {
        return ReactiveSecurityContextHolder.getContext()
            .doFirst(() -> log.info("인가 토큰 검증"))
            .map(SecurityContext::getAuthentication)
            .filter(auth -> auth instanceof UsernamePasswordAuthenticationToken)
            .cast(UsernamePasswordAuthenticationToken.class);

    }
}