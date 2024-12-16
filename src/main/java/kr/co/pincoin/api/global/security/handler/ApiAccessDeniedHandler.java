package kr.co.pincoin.api.global.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import kr.co.pincoin.api.global.exception.ErrorCode;
import kr.co.pincoin.api.global.response.error.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApiAccessDeniedHandler implements AccessDeniedHandler {
  private final ObjectMapper objectMapper;

  @Override
  public void handle(
      HttpServletRequest request,
      HttpServletResponse response,
      AccessDeniedException accessDeniedException)
      throws IOException {
    response.setStatus(HttpStatus.FORBIDDEN.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
    response.setCharacterEncoding("UTF-8");

    ErrorResponse errorResponse =
        ErrorResponse.of(
            request, ErrorCode.FORBIDDEN.getStatus(), ErrorCode.FORBIDDEN.getMessage());

    objectMapper.writeValue(response.getWriter(), errorResponse);
  }
}
