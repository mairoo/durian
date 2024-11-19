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

        HttpHeaders headers = new HttpHeaders();

        if (request.isRememberMe()) {
            ResponseCookie refreshTokenCookie = ResponseCookie.from(jwtProperties.cookieName(),
                                                                    tokenPair.getRefreshToken())
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(jwtProperties.refreshTokenExpiresIn())
                    .sameSite("Strict")
                    .domain(jwtProperties.cookieDomain())
                    .build();

            headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
        }

        return ResponseEntity.ok()
                .headers(headers)
                .body(ApiResponse.of(tokenPair.getAccessToken()));
    }

    // 리프레시
    @PostMapping("/refresh")
    public void
    refresh(@CookieValue(name = "refresh_token") String refreshToken) {
    }

    // 로그아웃
    @PostMapping("/sign-out")
    public void
    signOut() {
    }
}