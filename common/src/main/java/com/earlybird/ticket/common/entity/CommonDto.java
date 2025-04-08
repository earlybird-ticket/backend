package com.earlybird.ticket.common.entity;

import com.earlybird.ticket.common.entity.constant.Code;
import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommonDto<T> {

    private HttpStatus status;
    private String code;
    private String message;
    private T data;

    @Builder
    public CommonDto(HttpStatus status,
                     String code,
                     String message,
                     T data) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.data = data;
    }


    public static <T> CommonDto<T> created(@Nullable T data,
                                           String message) {
        return CommonDto.<T>builder()
                        .status(Code.CREATED.getHttpStatus())
                        .code(Code.CREATED.getCode())
                        .message(message)
                        .data(data)
                        .build();
    }

    public static <T> CommonDto<T> ok(@Nullable T data,
                                      String message) {
        return CommonDto.<T>builder()
                        .status(Code.OK.getHttpStatus())
                        .code(Code.OK.getCode())
                        .message(message)
                        .data(data)
                        .build();
    }


    public static <T> CommonDto<T> accepted(@Nullable T data,
                                            String message) {
        return CommonDto.<T>builder()
                        .status(Code.ACCEPTED.getHttpStatus())
                        .code(Code.ACCEPTED.getCode())
                        .message(message)
                        .data(data)
                        .build();
    }

    public static <T> CommonDto<T> fail(Code code,
                                        String message) {
        return CommonDto.<T>builder()
                        .status(code.getHttpStatus())
                        .code(code.getCode())
                        .message(message)
                        .build();
    }


}
