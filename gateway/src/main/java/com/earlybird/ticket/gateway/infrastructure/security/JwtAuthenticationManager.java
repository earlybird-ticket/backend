package com.earlybird.ticket.gateway.infrastructure.security;

import com.earlybird.ticket.common.entity.constant.Role;
import com.earlybird.ticket.gateway.infrastructure.TokenValidator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;

@Slf4j(topic = "토큰 추출")
@Component
@RequiredArgsConstructor
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    private static final String BEARER_PREFIX = "Bearer ";
    private final TokenValidator tokenValidator;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.just(authentication)
            .doFirst(() -> log.info("인증 전 Bearer prefix 제거"))
            .map(auth ->
                ((String) auth.getPrincipal()).substring(BEARER_PREFIX.length())
            )
            // 실제 검증 후 인증 객체 생성
            .doFirst(() -> log.info("JWT 인증 시작"))
            .doFinally(signalType -> {
                if (signalType == SignalType.ON_COMPLETE) {
                    log.info("JWT 인증 완료");
                } else {
                    log.info("JWT 인증 실패");
                }
            })
            .map(jwt -> {
                Role userRole = tokenValidator.getUserRole(jwt);
                Long userId = tokenValidator.getUserId(jwt);
                return new UsernamePasswordAuthenticationToken(
                    userId,
                    authentication.getCredentials(),
                    List.of(new SimpleGrantedAuthority("ROLE_" + userRole.getValue()))
                );
            });

    }
}