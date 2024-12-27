package kr.co.pincoin.api.global.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import kr.co.pincoin.api.global.exception.ErrorCode;
import kr.co.pincoin.api.global.exception.JwtAuthenticationException;
import kr.co.pincoin.api.global.response.error.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private static final RequestMatcher BANK_TRANSFER_WEBHOOK_PATH =
      new AntPathRequestMatcher("/payment/bank-transfer/callback");

  public static final String BEARER_PREFIX = "Bearer ";

  public static final String TOKEN_PREFIX = "Token ";

  @Value("${bank-transfer.token}")
  private String BANK_TRANSFER_TOKEN;

  private final JwtTokenProvider jwtTokenProvider;

  private final UserDetailsService userDetailsService;

  private final ObjectMapper objectMapper;

  @Override
  protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
    // 매 요청마다 JWT 검증 오버헤드 발생 - 불필요한 토큰 파싱과 서명 검증
    // 스프링 시큐리티 설정에서 permitAll() 한 경로라고 해서 토큰 검증 필터가 실행이 안 되는 것이 아님
    //
    // 1. JwtAuthenticationFilter (토큰 검증)
    // 2. UsernamePasswordAuthenticationFilter
    // 3. ... 다른 필터들 ...
    // 4. FilterSecurityInterceptor (URL 기반 permitAll() 등의 권한 설정

    // JWT 토큰 검증이 불필요한 공개 엔드포인트 목록
    List<RequestMatcher> publicMatchers =
        Arrays.asList(
            new AntPathRequestMatcher("/auth/**"),
            new AntPathRequestMatcher("/oauth2/**"),
            new AntPathRequestMatcher("/actuator/**"), // actuator는 이미 denyAll 되어있음
            new AntPathRequestMatcher("/products/**"),
            new AntPathRequestMatcher("/categories/**"),
            new AntPathRequestMatcher("/public/**"),
            new AntPathRequestMatcher("/api/**"),
            new AntPathRequestMatcher("/payment/billgate/callback"),
            new AntPathRequestMatcher("/payment/paypal/callback"),
            new AntPathRequestMatcher("/payment/danal/callback"));

    // 명시적으로 공개된 엔드포인트가 아니면 모두 필터링
    return publicMatchers.stream().anyMatch(matcher -> matcher.matches(request));
  }

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws IOException {
    try {
      boolean webhookPath = BANK_TRANSFER_WEBHOOK_PATH.matches(request);

      String bearerToken = getBearerToken(request);
      String apiToken = getToken(request);

      if (apiToken == null && bearerToken == null) {
        throw new JwtAuthenticationException(ErrorCode.UNAUTHORIZED);
      }

      if (webhookPath) {
        // API 토큰 경로인 경우
        processApiToken(apiToken);
      } else {
        // Bearer 토큰이 필요한 경로인 경우
        processBearerToken(bearerToken);
      }

      // 다음 필터 실행
      filterChain.doFilter(request, response);
    } catch (JwtAuthenticationException e) {
      SecurityContextHolder.clearContext();
      handleAuthenticationException(request, response, e);
    } catch (Exception e) {
      SecurityContextHolder.clearContext();
      handleAuthenticationException(
          request, response, new JwtAuthenticationException(ErrorCode.UNAUTHORIZED));
    }
  }

  private void processBearerToken(String token) {
    // JWT 토큰 유효성 확인 및 username 추출 후 인증 처리
    Optional.of(token).flatMap(jwtTokenProvider::validateToken).ifPresent(this::authenticateUser);
  }

  private void processApiToken(String token) {
    if (!BANK_TRANSFER_TOKEN.equals(token)) {
      throw new JwtAuthenticationException(ErrorCode.INVALID_CREDENTIALS);
    }
    // API 토큰이 유효한 경우 아무 작업도 하지 않음
    // - SecurityContextHolder에 인증 정보를 저장하지 않음
    log.debug("Valid API token provided");
  }

  private void authenticateUser(String username) {
    try {
      // 1. 데이터베이스에서 username 조회
      UserDetails userDetails = userDetailsService.loadUserByUsername(username);

      // 2. 인증 객체 생성
      Authentication auth =
          new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

      // 3. WebAuthenticationDetails, WebAuthenticationDetailsSource 저장
      // WebAuthenticationDetails 객체는 인증 요청과 관련된 웹 관련 정보
      // - RemoteAddress (클라이언트 IP 주소)
      // - SessionId (현재 세션 ID)
      // setDetails()의 활용 용도
      // - 특정 IP 주소나 지역에서의 접근 제한
      // - 세션 기반의 추가적인 보안 검증
      // - 사용자 행동 분석 및 로깅
      // - 감사(audit) 기록 생성
      // - 다중 요소 인증(MFA) 구현
      // - IP 기반 접근 제한이나 차단
      // authentication.setDetails(new
      // WebAuthenticationDetailsSource().buildDetails(request));

      // 4. 현재 인증된 사용자 정보를 보안 컨텍스트에 저장 = 로그인 처리
      SecurityContextHolder.getContext().setAuthentication(auth);

      log.debug("logged in: {}", auth.getPrincipal());
    } catch (UsernameNotFoundException e) {
      throw new JwtAuthenticationException(ErrorCode.INVALID_CREDENTIALS);
    }
  }

  private String getBearerToken(HttpServletRequest request) {
    // Header format
    // RFC 7235 standard header
    // Authorization: Bearer JWTString=
    final String header = request.getHeader(HttpHeaders.AUTHORIZATION);

    if (header != null && header.startsWith(BEARER_PREFIX)) {
      return header.substring(BEARER_PREFIX.length()).trim();
    }

    return null;
  }

  private String getToken(HttpServletRequest request) {
    final String header = request.getHeader(HttpHeaders.AUTHORIZATION);

    if (header != null && header.startsWith(TOKEN_PREFIX)) {
      return header.substring(TOKEN_PREFIX.length()).trim();
    }

    return null;
  }

  private void handleAuthenticationException(
      HttpServletRequest request, HttpServletResponse response, JwtAuthenticationException e)
      throws IOException {
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
    response.setCharacterEncoding("UTF-8");

    log.warn("[Authentication] 인증실패: {}", e.getMessage());

    ErrorResponse errorResponse =
        ErrorResponse.of(
            request, ErrorCode.UNAUTHORIZED.getStatus(), ErrorCode.UNAUTHORIZED.getMessage());

    objectMapper.writeValue(response.getWriter(), errorResponse);
  }
}
