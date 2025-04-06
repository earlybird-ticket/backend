package com.earlybird.ticket.common.exception;

import com.earlybird.ticket.common.entity.CommonDto;
import com.earlybird.ticket.common.entity.constant.ErrorCode;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ServerErrorFeignExceptionHandler implements FeignExceptionHandlerStrategy {

    @Override
    public boolean supports(FeignException e) {
        return e.status() >= 500;
    }

    @Override
    public ResponseEntity<CommonDto<Void>> handleException(FeignException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                CommonDto.<Void>builder()
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .code(ErrorCode.FEIGN_INTERNAL_SERVER_ERROR.value())
                        .message("서버 오류가 발생했습니다: " + e.getMessage())
                        .data(null)
                        .build()
        );
    }
}
