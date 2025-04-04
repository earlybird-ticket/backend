package com.earlybirdticket.common.entity;

import com.earlybirdticket.common.entity.constant.ErrorCode;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommonDto<T> {

    private HttpStatus status;
    private ErrorCode code;
    private String message;
    private T data;

    @Builder
    public CommonDto(
            HttpStatus status,
            ErrorCode code,
            String message,
            T data
    ) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
