package kr.co.pincoin.api.global.response.error.exception;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.pincoin.api.global.response.error.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    // 스프링 시큐리티에서 401, 403 오류는 직접 처리
    // handleAuthenticationException 메소드 - 401 재정의 안 함
    // handleAccessDeniedException 메소드 - 403 재정의 안 함

    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException e, HttpServletRequest request) {
        log.error("Business Exception: {}", e.getMessage());
        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(ErrorResponse.of(request,
                                       e.getErrorCode().getStatus(),
                                       e.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e, HttpServletRequest request) {
        log.error("Request Body Missing Exception: {}", e.getMessage());

        return ResponseEntity
                .status(ErrorCode.REQUEST_BODY_MISSING.getStatus())
                .body(ErrorResponse.of(request,
                                       ErrorCode.REQUEST_BODY_MISSING.getStatus(),
                                       ErrorCode.REQUEST_BODY_MISSING.getMessage()));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        log.error("Method Not Allowed Exception: {}", e.getMessage());

        return ResponseEntity
                .status(ErrorCode.METHOD_NOT_ALLOWED.getStatus())
                .body(ErrorResponse.of(request,
                                       ErrorCode.METHOD_NOT_ALLOWED.getStatus(),
                                       ErrorCode.METHOD_NOT_ALLOWED.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e, HttpServletRequest request) {
        log.error("Validation Exception: {}", e.getMessage());
        String errorMessage = e.getBindingResult().getFieldError() != null ?
                e.getBindingResult().getFieldError().getDefaultMessage() :
                "Invalid input value";

        return ResponseEntity
                .status(ErrorCode.INVALID_INPUT_VALUE.getStatus())
                .body(ErrorResponse.of(request,
                                       ErrorCode.INVALID_INPUT_VALUE.getStatus(),
                                       errorMessage));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    protected ResponseEntity<ErrorResponse> handleHttpMediaTypeNotSupportedException(
            HttpMediaTypeNotSupportedException e, HttpServletRequest request) {
        log.error("Unsupported Media Type Exception: {}", e.getMessage());

        return ResponseEntity
                .status(ErrorCode.UNSUPPORTED_MEDIA_TYPE.getStatus())
                .body(ErrorResponse.of(request,
                                       ErrorCode.UNSUPPORTED_MEDIA_TYPE.getStatus(),
                                       ErrorCode.UNSUPPORTED_MEDIA_TYPE.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(
            Exception e, HttpServletRequest request) {
        log.error("Internal Server Error: {}", e.getMessage());
        return ResponseEntity
                .status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
                .body(ErrorResponse.of(request,
                                       ErrorCode.INTERNAL_SERVER_ERROR.getStatus(),
                                       e.getMessage()));
    }
}
