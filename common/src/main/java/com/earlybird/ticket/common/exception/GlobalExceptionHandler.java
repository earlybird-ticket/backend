package com.earlybird.ticket.common.exception;

import com.earlybird.ticket.common.entity.CommonDto;
import com.earlybird.ticket.common.entity.constant.Code;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private static final String LOG_INFO_MESSAGE = "Exception = {}, {}";
    private final List<FeignExceptionHandlerStrategy> strategies;

    Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<CommonDto<Void>> handleValidationException(FeignException e) {
        log.error("error ={}",
                  e.getMessage(),
                  e);

        return strategies.stream()
                         .filter(strategy -> strategy.supports(e))
                         .findFirst()
                         .map(strategy -> strategy.handleException(e))
                         .orElseGet(() -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                                        .body(CommonDto.<Void>builder()
                                                                       .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                                                       .code(Code.FEIGN_INTERNAL_SERVER_ERROR.getCode())
                                                                       .message("Feign 오류 발생: " + e.getMessage())
                                                                       .data(null)
                                                                       .build()));
    }

    @ExceptionHandler(AbstractException.class)
    public ResponseEntity<CommonDto<Object>> handleCustomException(AbstractException e) {
        log.error("Custom Exception: {}",
                  e.getMessage(),
                  e);

        return ResponseEntity.status(e.getCode()
                                      .getHttpStatus())
                             .body(CommonDto.fail(e.getCode(),
                                                  e.getMessage()));
    }

    //    @ExceptionHandler({EntityNotFoundException.class})
    //    @ResponseStatus(HttpStatus.BAD_REQUEST)
    //    public CommonDto<Object> handleUserNotFoundException(EntityNotFoundException e) {
    //        log.error("error ={}", e.getMessage(), e);
    //        return CommonDto.builder()
    //                .status(HttpStatus.BAD_REQUEST)
    //                .code(HttpStatus.BAD_REQUEST.value())
    //                .message(e.getMessage()) // Ensure the message is included
    //                .data(null)
    //                .build();
    //    }
    //
    //    @ExceptionHandler({IOException.class})
    //    @ResponseStatus(HttpStatus.BAD_REQUEST)
    //    public CommonDto<Object> handleIOException(IOException e) {
    //        log.error("error ={}", e.getMessage(), e);
    //
    //        return CommonDto.builder()
    //                .status(HttpStatus.INTERNAL_SERVER_ERROR)
    //                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
    //                .message(e.getMessage()) // Ensure the message is included
    //                .data(null)
    //                .build();
    //    }
    //
    //
    //    @ExceptionHandler({MethodArgumentNotValidException.class})
    //    @ResponseStatus(HttpStatus.BAD_REQUEST)
    //    public CommonDto<Object> handleMethodArgumentNotValidException(
    //            MethodArgumentNotValidException e
    //    ) {
    //        log.error("error ={}", e.getMessage(), e);
    //        return CommonDto.builder()
    //                .status(HttpStatus.BAD_REQUEST)
    //                .code(HttpStatus.BAD_REQUEST.value())
    //                .message(e.getMessage()) // Ensure the message is included
    //                .data(null)
    //                .build();
    //    }
    //
    //    @ExceptionHandler(NullPointerException.class)
    //    private CommonDto<Object> handleNullPointerException
    //            (NullPointerException e) {
    //        log.error("error ={}", e.getMessage(), e);
    //
    //        return CommonDto.builder()
    //                .status(HttpStatus.INTERNAL_SERVER_ERROR)
    //                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
    //                .message(e.getMessage()) // Ensure the message is included
    //                .data(null)
    //                .build();
    //    }
    //
    //    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    //    private CommonDto<Object> handleHttpRequestMethodNotSupportedException
    //            (HttpRequestMethodNotSupportedException e) {
    //        log.error("error ={}", e.getMessage(), e);
    //
    //        return CommonDto.builder()
    //                .status(HttpStatus.METHOD_NOT_ALLOWED)
    //                .code(HttpStatus.METHOD_NOT_ALLOWED.value())
    //                .message(e.getMessage()) // Ensure the message is included
    //                .data(null)
    //                .build();
    //    }
    //
    //    @ExceptionHandler(AccessDeniedException.class)
    //    private CommonDto<Object> handleAccessDeniedException
    //            (AccessDeniedException e) {
    //        log.error("error ={}", e.getMessage(), e);
    //
    //        return CommonDto.builder()
    //                .status(HttpStatus.FORBIDDEN)
    //                .code(HttpStatus.FORBIDDEN.value())
    //                .message(e.getMessage()) // Ensure the message is included
    //                .data(null)
    //                .build();
    //    }
    //
    //    @ExceptionHandler(IllegalArgumentException.class)
    //    private CommonDto<Object> handleIllegalArgumentException
    //            (IllegalArgumentException e) {
    //        log.error("error ={}", e.getMessage(), e);
    //
    //        return CommonDto.builder()
    //                .status(HttpStatus.BAD_REQUEST)
    //                .code(HttpStatus.BAD_REQUEST.value())
    //                .message(e.getMessage()) // Ensure the message is included
    //                .data(null)
    //                .build();
    //    }
    //
    //    @ExceptionHandler(HttpMessageNotReadableException.class)
    //    private CommonDto<Object> handleHttpMessageNotReadableException
    //            (HttpMessageNotReadableException e) {
    //        log.error("error ={}", e.getMessage(), e);
    //
    //        return CommonDto.builder()
    //                .status(HttpStatus.BAD_REQUEST)
    //                .code(HttpStatus.BAD_REQUEST.value())
    //                .message(e.getMessage()) // Ensure the message is included
    //                .data(null)
    //                .build();
    //    }
    //
    //
    //    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    //    private CommonDto<Object> handleHttpMediaTypeNotSupportedException
    //            (HttpMediaTypeNotSupportedException e) {
    //        log.error("error ={}", e.getMessage(), e);
    //
    //        return CommonDto.builder()
    //                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    //                .code(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
    //                .message(e.getMessage()) // Ensure the message is included
    //                .data(null)
    //                .build();
    //    }
}

