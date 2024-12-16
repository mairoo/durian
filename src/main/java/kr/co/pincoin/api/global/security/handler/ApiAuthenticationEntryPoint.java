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
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApiAuthenticationEntryPoint implements AuthenticationEntryPoint {
  private final ObjectMapper objectMapper;

  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException {
    ErrorCode errorCode = ErrorCode.UNAUTHORIZED;

    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
    response.setCharacterEncoding("UTF-8");

    ErrorResponse errorResponse =
        ErrorResponse.of(request, errorCode.getStatus(), errorCode.getMessage());

    objectMapper.writeValue(response.getWriter(), errorResponse);
  }
}
