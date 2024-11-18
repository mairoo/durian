package kr.co.pincoin.api.global.response.error.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // Common
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "Invalid input value"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"),

    // Authentication
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "Invalid token"),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "Expired token"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Unauthorized access"),

    // Domain specific
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "Entity not found"),
    DUPLICATE_ENTITY(HttpStatus.CONFLICT, "Entity already exists"),
    BUSINESS_RULE_VIOLATION(HttpStatus.BAD_REQUEST, "Business rule violation");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}