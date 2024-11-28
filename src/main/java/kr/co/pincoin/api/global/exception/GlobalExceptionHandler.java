package kr.co.pincoin.api.global.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import kr.co.pincoin.api.global.response.error.ErrorResponse;
import kr.co.pincoin.api.global.response.error.ValidationError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    // handleAuthenticationException 메소드 - 재정의 안 함 401: JwtAuthenticationFilter 필터에서 직접 예외 처리
    // handleAccessDeniedException 메소드 - 재정의 안 함 403: 스프링 시큐리티 커스텀 예외 처리

    // 1. 비즈니스 / 애플리케이션 예외
    /**
     * 비즈니스 로직 예외 처리
     * 애플리케이션에서 정의한 비즈니스 규칙 위반 시 발생하는 예외 처리
     * 예) 잔액 부족, 재고 부족, 주문 취소 불가 등
     */
    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ErrorResponse>
    handleBusinessException(BusinessException e,
                            HttpServletRequest request) {
        log.error("[Business Exception] {}", e.getMessage());
        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(ErrorResponse.of(request,
                                       e.getErrorCode().getStatus(),
                                       e.getMessage()));
    }

    // 2. 인증 / 보안 예외
    /**
     * null 예외 처리
     * 인증/보안 예외처리
     */
    @ExceptionHandler(NullPointerException.class)
    protected ResponseEntity<ErrorResponse>
    handleNullPointerException(NullPointerException e,
                               HttpServletRequest request) {
        // User 객체 관련 NPE인 경우에는 인증 오류로 처리
        if (e.getMessage() != null && e.getMessage().contains("User.getId()")) {
            log.error("[Authentication Required] {}", e.getMessage());
            return ResponseEntity
                    .status(ErrorCode.AUTHENTICATION_REQUIRED.getStatus())
                    .body(ErrorResponse.of(request,
                                           ErrorCode.AUTHENTICATION_REQUIRED.getStatus(),
                                           ErrorCode.AUTHENTICATION_REQUIRED.getMessage()));
        }

        // 다른 NPE는 일반적인 서버 오류로 처리
        return handleInternalServerError(e, request);
    }

    // 3. 입력값 검증 예외
    /**
     * 입력값 유효성 검사 실패 처리
     * Bean Validation (@Valid, @Validated) 실패 시 발생하는 예외 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse>
    handleMethodArgumentNotValidException(MethodArgumentNotValidException e,
                                          HttpServletRequest request) {
        log.error("[Validation Exception] {}", e.getMessage());

        List<ValidationError> validationErrors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> ValidationError.builder()
                        .field(error.getField())
                        .message(error.getDefaultMessage())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity
                .status(ErrorCode.INVALID_INPUT_VALUE.getStatus())
                .body(ErrorResponse.of(request,
                                       ErrorCode.INVALID_INPUT_VALUE.getStatus(),
                                       ErrorCode.INVALID_INPUT_VALUE.getMessage(),
                                       validationErrors));
    }

    /**
     * JSON 파싱 오류 처리
     * 잘못된 JSON 형식이나 타입 불일치로 인한 파싱 실패 시 발생하는 예외 처리
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<ErrorResponse>
    handleHttpMessageNotReadableException(HttpMessageNotReadableException e,
                                          HttpServletRequest request) {
        log.error("[Request Body Missing] {}", e.getMessage());

        return ResponseEntity
                .status(ErrorCode.REQUEST_BODY_MISSING.getStatus())
                .body(ErrorResponse.of(request,
                                       ErrorCode.REQUEST_BODY_MISSING.getStatus(),
                                       ErrorCode.REQUEST_BODY_MISSING.getMessage()));
    }

    /**
     * 필수 쿠키가 없는 경우 예외 처리
     */
    @ExceptionHandler(MissingRequestCookieException.class)
    protected ResponseEntity<ErrorResponse>
    handleMissingRequestCookieException(MissingRequestCookieException e,
                                        HttpServletRequest request) {
        log.error("[Missing Cookie] {}", e.getMessage());
        return ResponseEntity
                .status(ErrorCode.REQUEST_COOKIE_MISSING.getStatus())
                .body(ErrorResponse.of(request,
                                       ErrorCode.REQUEST_COOKIE_MISSING.getStatus(),
                                       ErrorCode.REQUEST_COOKIE_MISSING.getMessage()));
    }

    // 4. HTTP 관련 예외
    /**
     * 정적 리소스를 찾지 못했을 때의 예외 처리
     */
    @ExceptionHandler(NoResourceFoundException.class)
    protected ResponseEntity<ErrorResponse>
    handleNoResourceFoundException(NoResourceFoundException e,
                                   HttpServletRequest request) {
        log.error("[Page Not Found] {}", e.getMessage());
        return ResponseEntity
                .status(ErrorCode.RESOURCE_NOT_FOUND.getStatus())
                .body(ErrorResponse.of(request,
                                       ErrorCode.RESOURCE_NOT_FOUND.getStatus(),
                                       ErrorCode.RESOURCE_NOT_FOUND.getMessage()));
    }

    /**
     * HTTP 메소드 오류 처리
     * 지원하지 않는 HTTP 메소드 호출 시 발생하는 예외 처리
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ErrorResponse>
    handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e,
                                                 HttpServletRequest request) {
        log.error("[Method Not Allowed] {}", e.getMessage());

        return ResponseEntity
                .status(ErrorCode.METHOD_NOT_ALLOWED.getStatus())
                .body(ErrorResponse.of(request,
                                       ErrorCode.METHOD_NOT_ALLOWED.getStatus(),
                                       ErrorCode.METHOD_NOT_ALLOWED.getMessage()));
    }

    /**
     * Content-Type 오류 처리
     * 지원하지 않는 Content-Type으로 요청 시 발생하는 예외 처리
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    protected ResponseEntity<ErrorResponse>
    handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e,
                                             HttpServletRequest request) {
        log.error("[Unsupported Media Type] {}", e.getMessage());

        return ResponseEntity
                .status(ErrorCode.UNSUPPORTED_MEDIA_TYPE.getStatus())
                .body(ErrorResponse.of(request,
                                       ErrorCode.UNSUPPORTED_MEDIA_TYPE.getStatus(),
                                       ErrorCode.UNSUPPORTED_MEDIA_TYPE.getMessage()));
    }

    /**
     * 파일 업로드 크기 제한 초과 처리
     * 설정된 최대 파일 크기를 초과하여 업로드 시 발생하는 예외 처리
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    protected ResponseEntity<ErrorResponse>
    handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e,
                                         HttpServletRequest request) {
        log.error("[File Size Exceeded] {}", e.getMessage());
        return ResponseEntity
                .status(ErrorCode.FILE_SIZE_EXCEEDED.getStatus())
                .body(ErrorResponse.of(request,
                                       ErrorCode.FILE_SIZE_EXCEEDED.getStatus(),
                                       ErrorCode.FILE_SIZE_EXCEEDED.getMessage()));
    }

    // 5. DB / 데이터 관련 예외
    /**
     * 엔티티 조회 실패 처리
     * JPA에서 엔티티를 찾지 못했을 때 발생하는 예외 처리
     */
    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<ErrorResponse>
    handleEntityNotFoundException(EntityNotFoundException e,
                                  HttpServletRequest request) {
        log.error("[Resource Not Found] {}", e.getMessage());
        return ResponseEntity
                .status(ErrorCode.RESOURCE_NOT_FOUND.getStatus())
                .body(ErrorResponse.of(request,
                                       ErrorCode.RESOURCE_NOT_FOUND.getStatus(),
                                       ErrorCode.RESOURCE_NOT_FOUND.getMessage()));
    }

    /**
     * 데이터 무결성 위반 처리
     * DB 제약조건 위반 시 발생하는 예외 처리 (UK, FK 등)
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    protected ResponseEntity<ErrorResponse>
    handleDataIntegrityViolationException(DataIntegrityViolationException e,
                                          HttpServletRequest request) {
        log.error("[Data Integrity Violation] Message: {}, Cause: {}",
                  e.getMessage(),
                  e.getMostSpecificCause().getMessage());

        if (e.getCause() instanceof SQLIntegrityConstraintViolationException cause) {
            return switch (cause.getErrorCode()) {
                case 1062 -> // Duplicate entry (UK/PK violation)
                        ResponseEntity
                                .status(ErrorCode.DUPLICATE_KEY.getStatus())
                                .body(ErrorResponse.of(request,
                                                       ErrorCode.DUPLICATE_KEY.getStatus(),
                                                       ErrorCode.DUPLICATE_KEY.getMessage()));
                case 1452 -> // FK violation
                        ResponseEntity
                                .status(ErrorCode.FOREIGN_KEY_VIOLATION.getStatus())
                                .body(ErrorResponse.of(request,
                                                       ErrorCode.FOREIGN_KEY_VIOLATION.getStatus(),
                                                       ErrorCode.FOREIGN_KEY_VIOLATION.getMessage()));
                default -> ResponseEntity
                        .status(ErrorCode.DATA_INTEGRITY_VIOLATION.getStatus())
                        .body(ErrorResponse.of(request,
                                               ErrorCode.DATA_INTEGRITY_VIOLATION.getStatus(),
                                               ErrorCode.DATA_INTEGRITY_VIOLATION.getMessage()));
            };
        }

        return ResponseEntity
                .status(ErrorCode.DATA_INTEGRITY_VIOLATION.getStatus())
                .body(ErrorResponse.of(request,
                                       ErrorCode.DATA_INTEGRITY_VIOLATION.getStatus(),
                                       ErrorCode.DATA_INTEGRITY_VIOLATION.getMessage()));
    }

    // 6. 일반적인 예외 (항상 마지막에 위치)
    /**
     * 기타 모든 예외 처리
     * 위에서 처리되지 않은 모든 예외를 처리하는 마지막 단계
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse>
    handleException(Exception e,
                    HttpServletRequest request) {
        return handleInternalServerError(e, request);
    }

    /**
     * 내부 서버 오류 공통 처리
     */
    private ResponseEntity<ErrorResponse> handleInternalServerError(Exception e, HttpServletRequest request) {
        log.error("[Internal Server Error] {}", e.getMessage());
        return ResponseEntity
                .status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
                .body(ErrorResponse.of(request,
                                       ErrorCode.INTERNAL_SERVER_ERROR.getStatus(),
                                       ErrorCode.INTERNAL_SERVER_ERROR.getMessage())); // 일반화된 메시지 사용
    }
}