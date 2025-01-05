package kr.co.pincoin.api.app.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kr.co.pincoin.api.app.auth.request.SignInRequest;
import kr.co.pincoin.api.app.auth.response.AccessTokenResponse;
import kr.co.pincoin.api.app.auth.service.AuthService;
import kr.co.pincoin.api.domain.auth.vo.TokenPair;
import kr.co.pincoin.api.global.constant.CookieKey;
import kr.co.pincoin.api.global.response.success.ApiResponse;
import kr.co.pincoin.api.global.security.jwt.JwtProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
  private final AuthService authService;

  private final JwtProperties jwtProperties;

  /**
   * 사용자 로그인을 처리하고 액세스 토큰과 리프레시 토큰을 발급합니다.
   *
   * @param request 로그인 요청 정보 (이메일, 비밀번호 등)
   * @param servletRequest HTTP 요청 객체
   * @return ResponseEntity 객체로 래핑된 액세스 토큰과 쿠키에 설정된 리프레시 토큰
   */
  @PostMapping("/sign-in")
  public ResponseEntity<ApiResponse<AccessTokenResponse>> signIn(
      @Valid @RequestBody SignInRequest request, HttpServletRequest servletRequest) {
    TokenPair tokenPair = authService.login(request, servletRequest);

    return ResponseEntity.ok()
        .headers(createRefreshTokenCookie(tokenPair.getRefreshToken()))
        .body(ApiResponse.of(tokenPair.getAccessToken()));
  }

  /**
   * 리프레시 토큰을 사용하여 새로운 액세스 토큰과 리프레시 토큰을 발급합니다.
   *
   * @param refreshToken 쿠키에서 추출한 리프레시 토큰
   * @param servletRequest HTTP 요청 객체
   * @return ResponseEntity 객체로 래핑된 새로운 액세스 토큰과 쿠키에 설정된 새로운 리프레시 토큰
   */
  @PostMapping("/refresh")
  public ResponseEntity<ApiResponse<AccessTokenResponse>> refresh(
      @CookieValue(name = CookieKey.REFRESH_TOKEN_NAME) String refreshToken,
      HttpServletRequest servletRequest) {
    TokenPair tokenPair = authService.refresh(refreshToken, servletRequest);

    return ResponseEntity.ok()
        .headers(createRefreshTokenCookie(tokenPair.getRefreshToken()))
        .body(ApiResponse.of(tokenPair.getAccessToken()));
  }

  /**
   * 사용자 로그아웃을 처리하고 리프레시 토큰을 무효화합니다.
   *
   * @param refreshToken 쿠키에서 추출한 리프레시 토큰 (선택적)
   * @return ResponseEntity 객체와 리프레시 토큰 쿠키를 제거하는 응답
   */
  @PostMapping("/sign-out")
  public ResponseEntity<ApiResponse<Void>> signOut(
      @CookieValue(name = CookieKey.REFRESH_TOKEN_NAME, required = false) String refreshToken) {
    authService.logout(refreshToken);

    return ResponseEntity.ok()
        .headers(createRefreshTokenCookie("")) // 쿠키 삭제 효과
        .body(ApiResponse.of(null));
  }

  /**
   * 리프레시 토큰을 포함하는 HTTP 쿠키를 생성합니다.
   *
   * @param refreshToken 설정할 리프레시 토큰 값
   * @return 리프레시 토큰 쿠키가 포함된 HTTP 헤더
   */
  private HttpHeaders createRefreshTokenCookie(String refreshToken) {
    HttpHeaders headers = new HttpHeaders();

    ResponseCookie cookie =
        ResponseCookie.from(CookieKey.REFRESH_TOKEN_NAME, refreshToken != null ? refreshToken : "")
            .httpOnly(true)
            .secure(true)
            .path(CookieKey.PATH)
            .maxAge(
                refreshToken != null && !refreshToken.isEmpty()
                    ? jwtProperties.refreshTokenExpiresIn()
                    : 0)
            .sameSite(CookieKey.SAME_SITE)
            .domain(jwtProperties.cookieDomain())
            .build();

    headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
    return headers;
  }
}
