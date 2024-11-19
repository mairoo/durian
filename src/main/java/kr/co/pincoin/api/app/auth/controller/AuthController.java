package kr.co.pincoin.api.app.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kr.co.pincoin.api.app.auth.request.SignInRequest;
import kr.co.pincoin.api.app.auth.response.AccessTokenResponse;
import kr.co.pincoin.api.app.auth.service.AuthService;
import kr.co.pincoin.api.domain.auth.vo.TokenPair;
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

    @PostMapping("/sign-in")
    public ResponseEntity<ApiResponse<AccessTokenResponse>>
    signIn(@Valid @RequestBody SignInRequest request,
           HttpServletRequest servletRequest) {
        TokenPair tokenPair = authService.login(request, servletRequest);

        return ResponseEntity.ok()
                .headers(createRefreshTokenCookie(tokenPair.getRefreshToken()))
                .body(ApiResponse.of(tokenPair.getAccessToken()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AccessTokenResponse>>
    refresh(@CookieValue(name = JwtProperties.REFRESH_TOKEN_COOKIE_NAME) String refreshToken,
            HttpServletRequest servletRequest) {
        TokenPair tokenPair = authService.refresh(refreshToken, servletRequest);

        return ResponseEntity.ok()
                .headers(createRefreshTokenCookie(tokenPair.getRefreshToken()))
                .body(ApiResponse.of(tokenPair.getAccessToken()));
    }

    @PostMapping("/sign-out")
    public ResponseEntity<ApiResponse<Void>> signOut(
            @CookieValue(name = JwtProperties.REFRESH_TOKEN_COOKIE_NAME, required = false) String refreshToken) {
        authService.logout(refreshToken);

        return ResponseEntity.ok()
                .headers(createRefreshTokenCookie(""))
                .body(ApiResponse.of(null));
    }

    private HttpHeaders createRefreshTokenCookie(String refreshToken) {
        HttpHeaders headers = new HttpHeaders();

        ResponseCookie cookie = ResponseCookie.from(JwtProperties.REFRESH_TOKEN_COOKIE_NAME,
                                                    refreshToken != null ? refreshToken : "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshToken != null && !refreshToken.isEmpty()
                                ? jwtProperties.refreshTokenExpiresIn() : 0)
                .sameSite("Strict")
                .domain(jwtProperties.cookieDomain())
                .build();

        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
        return headers;
    }
}