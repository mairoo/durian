package kr.co.pincoin.api.global.security.annotation;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@AuthenticationPrincipal(expression = "user")
public @interface CurrentUser {
    // @AuthenticationPrincipal(expression = "user") User user
    // UserDetailsAdapter, UserDetailsServiceAdapter 어댑터 패턴 사용에 따른 문제 해결
}