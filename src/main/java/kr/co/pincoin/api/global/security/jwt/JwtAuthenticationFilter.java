package kr.co.pincoin.api.global.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
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
  public static final String BEARER_PREFIX = "Bearer ";
  public static final String X_AUTH_TOKEN = "X-Auth-Token";
  private final JwtTokenProvider jwtTokenProvider;
  private final UserDetailsService userDetailsService;
  private final ObjectMapper objectMapper;

  @PostConstruct
  public void setup() {
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  }

  @Override
  protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
    // 매 요청마다 JWT 검증 오버헤드 발생 - 불필요한 토큰 파싱과 서명 검증
    // 스프링 시큐리티 설정에서 permitAll() 한 경로라고 해서 토큰 검증 필터가 실행이 안 되는 것이 아님
    //
    // 1. JwtAuthenticationFilter (토큰 검증)
    // 2. UsernamePasswordAuthenticationFilter
    // 3. ... 다른 필터들 ...
    // 4. FilterSecurityInterceptor (URL 기반 permitAll() 등의 권한 설정

    // 완전 공개 리소스 목록
    List<RequestMatcher> permitAllMatchers =
        Arrays.asList(
            new AntPathRequestMatcher("/auth/**"),
            new AntPathRequestMatcher("/oauth2/**"),
            new AntPathRequestMatcher("/products/**"),
            new AntPathRequestMatcher("/categories/**"),
            new AntPathRequestMatcher("/public/**"));

    return permitAllMatchers.stream().anyMatch(matcher -> matcher.matches(request));
  }

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws IOException {
    try {
      // 1. HTTP 프로토콜 헤더에서 토큰 추출
      Optional.ofNullable(getBearerToken(request))
          // 2. JWT 토큰 유효성 확인 및 username 추출
          .flatMap(jwtTokenProvider::validateToken)
          //
          .ifPresent(
              sub -> {
                try {
                  // 3. 데이터베이스에서 username 조회
                  UserDetails userDetails = userDetailsService.loadUserByUsername(sub);

                  // 5. 인증 객체 생성
                  Authentication auth =
                      new UsernamePasswordAuthenticationToken(
                          userDetails, null, userDetails.getAuthorities());

                  // 6. WebAuthenticationDetails, WebAuthenticationDetailsSource 저장
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

                  // 7. 현재 인증된 사용자 정보를 보안 컨텍스트에 저장 = 로그인 처리
                  SecurityContextHolder.getContext().setAuthentication(auth);

                  log.debug("logged in: {}", auth.getPrincipal());
                } catch (UsernameNotFoundException e) {
                  throw new JwtAuthenticationException(ErrorCode.INVALID_CREDENTIALS);
                }
              });

      // 8. 이후 필터 실행
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

  // 토큰 추출 우선순위 설정
  private String extractToken(HttpServletRequest request) {
    // Bearer 토큰과 쿠키의 우선순위:
    // - 어떤 것을 먼저 검사할지 명확히 정의
    // - 동시에 존재할 경우의 처리 방침 수립

    // 1. Bearer 토큰 먼저 확인
    String bearerToken = getBearerToken(request);
    if (bearerToken != null) {
      return bearerToken;
    }

    // 2. 쿠키 확인
    return getCookieToken(request);
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

  private String getCookieToken(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if ("accessToken".equals(cookie.getName())) {
          return cookie.getValue();
        }
      }
    }
    return null;
  }

  private String getXAuthToken(HttpServletRequest request) {
    // Header format
    // Non-standard header
    // X-Auth-Token : JWTString=
    final String header = request.getHeader(X_AUTH_TOKEN);

    if (header != null && !header.isBlank()) {
      return header;
    }
    return null;
  }

  private void handleAuthenticationException(
      HttpServletRequest request, HttpServletResponse response, JwtAuthenticationException e)
      throws IOException {
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
    response.setCharacterEncoding("UTF-8");

    log.warn("[JWT] 인증실패: {}", e.getMessage());

    ErrorResponse errorResponse =
        ErrorResponse.of(
            request, ErrorCode.UNAUTHORIZED.getStatus(), ErrorCode.UNAUTHORIZED.getMessage());

    objectMapper.writeValue(response.getWriter(), errorResponse);
  }
}
