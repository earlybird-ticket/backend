package com.earlybird.ticket.common.exception;

import com.earlybird.ticket.common.entity.CommonDto;
import com.earlybird.ticket.common.entity.constant.ErrorCode;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class NotFoundFeignExceptionHandler implements FeignExceptionHandlerStrategy {

    @Override
    public boolean supports(FeignException e) {
        return e.status() == HttpStatus.NOT_FOUND.value();
    }

    @Override
    public ResponseEntity<CommonDto<Void>> handleException(FeignException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                CommonDto.<Void>builder()
                        .status(HttpStatus.NOT_FOUND)
                        .code(ErrorCode.FEIGN_NOT_FOUND.value())
                        .message("해당 데이터를 찾을 수 없습니다: " + e.getMessage())
                        .data(null)
                        .build()
        );
    }
}
