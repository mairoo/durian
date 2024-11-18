package kr.co.pincoin.api.app.auth.controller;

import jakarta.validation.Valid;
import kr.co.pincoin.api.app.auth.request.SignInRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    // 로그인
    @PostMapping("/sign-in")
    public void
    signIn(@Valid @RequestBody SignInRequest request) {
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