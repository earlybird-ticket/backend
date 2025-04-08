package com.earlybird.ticket.auth.application;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class TokenProvider {


    private static final String AUTHORIZATION_KEY = "auth";
    private static final String ISSUER = "EarlyBird";

    private final SecretKey key;
    private final Integer accessExp;

    public TokenProvider(@org.springframework.beans.factory.annotation.Value("${jwt.secret}") String salt,
                         @Value("${jwt.access.exp}") Integer accessExp) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(salt));
        this.accessExp = accessExp;
    }

    public String generateAccessToken(Long userId,
                                      String authorities) {
        Date now = new Date();

        return Jwts.builder()
                   .subject(Long.toString(userId))
                   .claim(AUTHORIZATION_KEY,
                          authorities)
                   .issuer(ISSUER)
                   .issuedAt(now)
                   .expiration(new Date(now.getTime() + accessExp))
                   .signWith(key,
                             Jwts.SIG.HS512)
                   .compact();
    }
}