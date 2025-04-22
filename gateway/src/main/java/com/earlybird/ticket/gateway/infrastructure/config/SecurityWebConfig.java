package com.earlybird.ticket.gateway.infrastructure.config;

import com.earlybird.ticket.gateway.infrastructure.security.JwtAuthenticationManager;
import com.earlybird.ticket.gateway.infrastructure.security.JwtServerAuthenticationConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.CsrfSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.FormLoginSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.HttpBasicSpec;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

@Configuration
@EnableWebFluxSecurity
public class SecurityWebConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
        ServerHttpSecurity http,
        ReactiveAuthenticationManager jwtAuthenticationManager,
        ServerAuthenticationConverter jwtAuthenticationConverter
    ) {
        http.csrf(CsrfSpec::disable)
            .formLogin(FormLoginSpec::disable)
            .httpBasic(HttpBasicSpec::disable)
            .securityContextRepository(
                NoOpServerSecurityContextRepository.getInstance()
            );

        // TODO: JWT 관련 Maanger, Converter 연결
        AuthenticationWebFilter authenticationWebFilter =
            new AuthenticationWebFilter(jwtAuthenticationManager);

        authenticationWebFilter.setServerAuthenticationConverter(jwtAuthenticationConverter);
        // 아래 경로에 대해서만 인증 진행
        // 명시하지 않는 경우 모든 경로에 대해서 인증 진행
        authenticationWebFilter.setRequiresAuthenticationMatcher(
            ServerWebExchangeMatchers.pathMatchers(
                "api/v1/seats/**",
                "api/v1/venues/**",
                "api/v1/admin/**",
                "api/v1/users/**",
                "api/v1/reservations/**",
                "api/v1/concerts/**",
                "api/v1/concertsequence/**",
                "api/v1/coupons/**",
                "api/v1/seats/**"
            )
        );

        // 생성한 필터 등록
        http.addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION);

        // TODO: 추후 권한별 분기 추가
        // 인가 경로
        http.authorizeExchange(exchanges -> exchanges
            // 테스트 위해 Payment 경로 허용
            .pathMatchers("api/v1/auth/**").permitAll()
            .pathMatchers("api/v1/payments/**").permitAll()
            .anyExchange().authenticated()
        );

        return http.build();
    }

}
