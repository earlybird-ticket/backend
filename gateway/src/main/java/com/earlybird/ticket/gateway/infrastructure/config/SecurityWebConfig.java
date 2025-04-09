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

        // 생성한 필터 등록
        http.addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION);

        // TODO: 추후 권한별 분기 추가
        http.authorizeExchange(exchanges -> exchanges
            .pathMatchers("api/v1/auth/**").permitAll()
            .anyExchange().authenticated()
        );

        return http.build();
    }

}
