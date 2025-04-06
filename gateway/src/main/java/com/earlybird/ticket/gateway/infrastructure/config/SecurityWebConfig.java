package com.earlybird.ticket.gateway.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.CsrfSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.FormLoginSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.HttpBasicSpec;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

@Configuration
@EnableWebFluxSecurity
public class SecurityWebConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http.csrf(CsrfSpec::disable)
            .formLogin(FormLoginSpec::disable)
            .httpBasic(HttpBasicSpec::disable)
            .securityContextRepository(
                NoOpServerSecurityContextRepository.getInstance()
            );

        http.authorizeExchange(exchanges -> exchanges
//                    .pathMatchers("api/v1/auth/**").permitAll()
                .anyExchange().permitAll()
        );

        return http.build();
    }

}
