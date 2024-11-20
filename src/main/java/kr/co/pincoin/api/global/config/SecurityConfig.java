package kr.co.pincoin.api.global.config;

import kr.co.pincoin.api.global.security.handler.ApiAccessDeniedHandler;
import kr.co.pincoin.api.global.security.handler.ApiAuthenticationEntryPoint;
import kr.co.pincoin.api.global.security.jwt.JwtAuthenticationFilter;
import kr.co.pincoin.api.infra.auth.support.DjangoPasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final ApiAuthenticationEntryPoint authenticationEntryPoint;

    private final ApiAccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. HTTP 보안 헤더 설정
                .headers(headers -> {
                    // 1-1. 기본 헤더 비활성화 후 필요한 것만 설정
                    headers.defaultsDisabled();

                    // 1-2. HTTPS 강제 설정 (프로덕션 환경에서 필수)
                    headers.httpStrictTransportSecurity(hstsConfig -> hstsConfig
                            .includeSubDomains(true)
                            .maxAgeInSeconds(31536000) // 1년
                            .preload(true));

                    // 1-3. Content-Type 스니핑 방지 (API 응답의 Content-Type을 보호)
                    headers.contentTypeOptions(contentTypeOptionsConfig -> {
                    });

                    // 1-4. 캐시 제어 (인증된 데이터가 캐시되는 것을 방지)
                    headers.cacheControl(cacheControlConfig -> {
                    });
                })

                // 2. 스프링 시큐리티 핵심 보안 메커니즘 설정: JwtAuthenticationFilter 토큰 검증과 OAuth2 사용

                // 2-1. CSRF 비활성화
                .csrf(AbstractHttpConfigurer::disable)

                // 2-2. Form 로그인 비활성화
                .formLogin(AbstractHttpConfigurer::disable)

                // 2-3. HTTP Basic 인증 비활성화
                .httpBasic(AbstractHttpConfigurer::disable)

                // 2-4. Remember-Me 비활성화
                .rememberMe(AbstractHttpConfigurer::disable)

                // 2-5. Anonymous 인증 비활성화
                .anonymous(AbstractHttpConfigurer::disable)

                // 2-6. OAuth2 인증 설정

                // HTTP Digest 인증 비활성화 (기본적으로 비활성화됨)
                // x509 인증 비활성화 (기본적으로 비활성화됨)

                // 3. 세션 관리 비활성화
                .sessionManagement(session ->
                                           session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 4. URL 접근 권한 설정 (모두 허용)
                // - JWT + 서비스 계층 메소드 보안으로 이미 충분한 보안 계층 존재
                // - REST API URL 패턴이 리소스 중심으로 설계되어 권한 관리가 너무 복잡해짐
                // - /users, /products 같은 엔드포인트는 인증/미인증 요청을 모두 처리해야하는 경우가 많음
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**").denyAll() // actuator 등 민감한 엔드포인트는 명시적 차단
                        .requestMatchers("/admin/**").authenticated() // 관리자 대시보드 인증 필요
                        .requestMatchers("/users/me").authenticated() // 사용자 경로 인증 필요
                        .anyRequest().permitAll()) // 나머지 모두 허용

                // 5. 커스텀 필터 추가 (JWT 인증)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                // 6. 예외 처리 설정 (401, 403 오류는 GlobalExceptionHandler 사용 안 함)
                // 401 - 토큰 검증 실패 시 JwtAuthenticationFilter 토큰 검증에서 직접 처리
                // 401 - OAuth2 인증 실패 시 스프링 시큐리티 커스텀 설정에서 처리
                // 403 - 권한 부족 시 스프링 시큐리티 커스텀 설정에서 처리
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint) // 401
                        .accessDeniedHandler(accessDeniedHandler)); // 403

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new DjangoPasswordEncoder();
    }
}