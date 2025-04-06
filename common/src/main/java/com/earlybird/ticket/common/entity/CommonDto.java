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
    private Code code;
    private String message;
    private T data;

    @Builder
    public CommonDto(HttpStatus status,
                     Code code,
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
                        .status(HttpStatus.CREATED)
                        .code(Code.CREATED)
                        .message(message)
                        .data(data)
                        .build();
    }

    public static <T> CommonDto<T> ok(@Nullable T data,
                                      String message) {
        return CommonDto.<T>builder()
                        .status(HttpStatus.OK)
                        .code(Code.OK)
                        .message(message)
                        .data(data)
                        .build();
    }


    public static <T> CommonDto<T> accepted(@Nullable T data,
                                            String message) {
        return CommonDto.<T>builder()
                        .status(HttpStatus.ACCEPTED)
                        .code(Code.ACCEPTED)
                        .message(message)
                        .data(data)
                        .build();
    }

    public static <T> CommonDto<T> fail(Code code,
                                        String message) {
        return CommonDto.<T>builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .code(code)
                        .message(message)
                        .build();
    }


}
