package kr.co.pincoin.api.global.response.error;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Builder
public class ErrorResponse {
    private final Long timestamp;  // Unix 타임스탬프
    private final int status;
    private final String error;
    private final String message;
    private final String path;

    public static ErrorResponse of(HttpServletRequest request, HttpStatus status, String message) {
        return ErrorResponse.builder()
                .timestamp(System.currentTimeMillis())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(request.getRequestURI())
                .build();
    }
}
