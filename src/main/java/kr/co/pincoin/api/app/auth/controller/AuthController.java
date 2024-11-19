package kr.co.pincoin.api.app.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kr.co.pincoin.api.app.auth.request.SignInRequest;
import kr.co.pincoin.api.app.auth.response.AccessTokenResponse;
import kr.co.pincoin.api.app.auth.service.AuthService;
import kr.co.pincoin.api.domain.auth.vo.TokenPair;
import kr.co.pincoin.api.global.response.success.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;

    @PostMapping("/sign-in")
    public ResponseEntity<ApiResponse<AccessTokenResponse>>
    signIn(@Valid @RequestBody SignInRequest request,
           HttpServletRequest servletRequest) {

        authService.login(request, servletRequest);


        return ResponseEntity.ok(ApiResponse.of(AccessTokenResponse.from(new TokenPair("accessToken",
                                                                                       null,
                                                                                       LocalDateTime.now()))));
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