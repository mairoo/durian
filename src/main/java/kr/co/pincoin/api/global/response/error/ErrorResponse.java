package kr.co.pincoin.api.global.response.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
  private final Long timestamp; // Unix 타임스탬프

  private final int status;

  private final String error;

  private final String message;

  private final String path;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private final List<ValidationError> errors; // Validation 오류일 때만 포함

  public static ErrorResponse of(HttpServletRequest request, HttpStatus status, String message) {
    return ErrorResponse.builder()
        .timestamp(System.currentTimeMillis())
        .status(status.value())
        .error(status.getReasonPhrase())
        .message(message)
        .path(request.getRequestURI())
        .build();
  }

  public static ErrorResponse of(
      HttpServletRequest request, HttpStatus status, String message, List<ValidationError> errors) {
    return ErrorResponse.builder()
        .timestamp(System.currentTimeMillis())
        .status(status.value())
        .error(status.getReasonPhrase())
        .message(message)
        .path(request.getRequestURI())
        .errors(errors)
        .build();
  }
}
