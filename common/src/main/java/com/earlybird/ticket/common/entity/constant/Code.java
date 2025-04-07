package com.earlybird.ticket.common.entity.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum Code {

    // 🔐 인증 관련
    ACCESS_TOKEN_EXPIRED("A01",
                         HttpStatus.UNAUTHORIZED),
    ACCESS_TOKEN_INVALID("A02",
                         HttpStatus.UNAUTHORIZED),
    ACCESS_TOKEN_NOT_FOUND("A03",
                           HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_EXPIRED("A04",
                          HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_INVALID("A05",
                          HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_NOT_FOUND("A06",
                            HttpStatus.UNAUTHORIZED),

    // 🚫 일반 에러
    FORBIDDEN("G01",
              HttpStatus.FORBIDDEN),
    UNAUTHORIZED("G02",
                 HttpStatus.UNAUTHORIZED),
    BAD_REQUEST("G03",
                HttpStatus.BAD_REQUEST),
    NOT_FOUND("G04",
              HttpStatus.NOT_FOUND),
    CONFLICT("G05",
             HttpStatus.CONFLICT),

    // 🔄 Feign 에러
    FEIGN_BAD_REQUEST("F01",
                      HttpStatus.BAD_REQUEST),
    FEIGN_NOT_FOUND("F02",
                    HttpStatus.NOT_FOUND),
    FEIGN_CONFLICT("F03",
                   HttpStatus.CONFLICT),
    FEIGN_UNAUTHORIZED("F04",
                       HttpStatus.UNAUTHORIZED),
    FEIGN_INTERNAL_SERVER_ERROR("F05",
                                HttpStatus.INTERNAL_SERVER_ERROR),

    // ✅ 정상 응답
    OK("E01",
       HttpStatus.OK),
    CREATED("E02",
            HttpStatus.CREATED),
    ACCEPTED("E03",
             HttpStatus.ACCEPTED);

    private final String code;
    private final HttpStatus httpStatus;

    public Code value() {
        return this;
    }
}