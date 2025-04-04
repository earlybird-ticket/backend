package com.earlybirdticket.common.exception;

import com.earlybirdticket.common.entity.CommonDto;
import com.earlybirdticket.common.entity.constant.ErrorCode;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class BadRequestFeignExceptionHandler implements FeignExceptionHandlerStrategy {

    @Override
    public boolean supports(FeignException e) {
        return e.status() == HttpStatus.BAD_REQUEST.value();
    }

    @Override
    public ResponseEntity<CommonDto<Void>> handleException(FeignException e) {
        return ResponseEntity.badRequest().body(
                CommonDto.<Void>builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .code(ErrorCode.FEIGN_BAD_REQUEST.value())
                        .message("잘못된 요청입니다: " + e.getMessage())
                        .data(null)
                        .build()
        );
    }
}
