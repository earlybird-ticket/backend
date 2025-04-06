package com.earlybird.ticket.gateway.infrastructure;

import com.earlybird.ticket.common.entity.constant.Role;
import com.earlybird.ticket.gateway.domain.TokenStatus;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j(topic = "토큰 검증")
@Component
public class TokenValidator {

    private final SecretKey key;
    private final String AUTHORIZATION_KEY = "auth";

    public TokenValidator(@Value("${jwt.secret}") String salt) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(salt));
    }

    /**
     * 토큰 상태 검증 메서드
     *
     * @param token 전달받은 JWT 토큰
     * @return - IS_VALID : 액세스 토큰 정상, 사용 가능 <br> - IS_EXPIRED : 액세스 토큰 만료됨, 재발급 필요 <br> -
     * IS_NOT_VALID : 액세스 토큰 사용 불가능, 재인증 필요
     */
    public TokenStatus validateAccessToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);

            return TokenStatus.VALID;
        } catch (SignatureException | MalformedJwtException e) {
            log.error("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 서명입니다. 토큰 재발급이 필요합니다.");
            return TokenStatus.EXPIRED;
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 서명입니다.");
        } catch (IllegalArgumentException e) {
            log.error("JWT 토큰이 잘못 되었습니다.");
        }
        return TokenStatus.INVALID;
    }

    // 데이터 추출
    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(accessToken)
                .getPayload();
        } catch (JwtException e) {
            throw new RuntimeException("JWT 토큰이 잘못 되었습니다.");
        }
    }

    // userId 추출
    public Long getUserId(String accessToken) {
        return Long.valueOf(parseClaims(accessToken).getSubject());
    }

    /**
     * Jwt 토큰 내의 auth 클레임을 기준으로 사용자 역할 부여
     *
     * @param accessToken
     * @return 부여받은 역할군
     */
    public Role getUserRole(String accessToken) {
        return Role.from(parseClaims(accessToken).get(AUTHORIZATION_KEY, String.class));
    }

    public LocalDateTime getIssuedAt(String accessToken) {
        Date issuedAt = parseClaims(accessToken).getIssuedAt();

        return issuedAt.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime();
    }
}
