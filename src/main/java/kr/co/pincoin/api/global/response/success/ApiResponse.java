package kr.co.pincoin.api.global.response.success;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
  private final Long timestamp;

  private final int status;

  private final String message;

  private final T data;

  public static <T> ApiResponse<T> of(T data) {
    return ApiResponse.<T>builder()
        .timestamp(System.currentTimeMillis())
        .status(HttpStatus.OK.value())
        .message("Success")
        .data(data)
        .build();
  }

  public static <T> ApiResponse<T> of(T data, String message) {
    return ApiResponse.<T>builder()
        .timestamp(System.currentTimeMillis())
        .status(HttpStatus.OK.value())
        .message(message)
        .data(data)
        .build();
  }

  public static <T> ApiResponse<T> of(T data, HttpStatus status) {
    return ApiResponse.<T>builder()
        .timestamp(System.currentTimeMillis())
        .status(status.value())
        .message(status.getReasonPhrase())
        .data(data)
        .build();
  }

  public static <T> ApiResponse<T> of(T data, HttpStatus status, String message) {
    return ApiResponse.<T>builder()
        .timestamp(System.currentTimeMillis())
        .status(status.value())
        .message(message)
        .data(data)
        .build();
  }
}
