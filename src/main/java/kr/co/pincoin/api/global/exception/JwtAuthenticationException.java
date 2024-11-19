package kr.co.pincoin.api.global.exception;

import lombok.Getter;

@Getter
public class JwtAuthenticationException extends RuntimeException {
    private final ErrorCode errorCode;

    public JwtAuthenticationException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public JwtAuthenticationException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
