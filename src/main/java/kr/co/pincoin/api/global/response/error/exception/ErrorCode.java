package kr.co.pincoin.api.global.response.error.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // 공통
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "잘못된 입력값입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),

    // 인증
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),  // 추가

    // 도메인 엔티티
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "엔티티를 찾을 수 없습니다."),
    DUPLICATE_ENTITY(HttpStatus.CONFLICT, "이미 존재하는 엔티티입니다."),
    BUSINESS_RULE_VIOLATION(HttpStatus.BAD_REQUEST, "비즈니스 규칙 위반입니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}