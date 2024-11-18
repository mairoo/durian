package kr.co.pincoin.api.global.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. 기본적인 보안 메커니즘 비활성화

                // 1-1. CSRF 비활성화
                .csrf(AbstractHttpConfigurer::disable)

                // 1-2. Form 로그인 비활성화
                .formLogin(AbstractHttpConfigurer::disable)

                // 1-3. HTTP Basic 인증 비활성화
                .httpBasic(AbstractHttpConfigurer::disable)

                // 1-4. Remember-Me 비활성화
                .rememberMe(AbstractHttpConfigurer::disable)

                // 1-5. Anonymous 인증 비활성화
                .anonymous(AbstractHttpConfigurer::disable)

                // HTTP Digest 인증 비활성화 (기본적으로 비활성화됨)
                // x509 인증 비활성화 (기본적으로 비활성화됨)

                // 2. 세션 관리 비활성화
                .sessionManagement(session ->
                                           session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 3. HTTP 보안 헤더 설정
                .headers(headers -> {
                    // 3-1. 기본 헤더 비활성화 후 필요한 것만 설정
                    headers.defaultsDisabled();

                    // 3-2. HTTPS 강제 설정 (프로덕션 환경에서 필수)
                    headers.httpStrictTransportSecurity(hstsConfig -> hstsConfig
                            .includeSubDomains(true)
                            .maxAgeInSeconds(31536000) // 1년
                            .preload(true));

                    // 3-3. Content-Type 스니핑 방지 (API 응답의 Content-Type을 보호)
                    headers.contentTypeOptions(contentTypeOptionsConfig -> {
                    });

                    // 3-4. 캐시 제어 (인증된 데이터가 캐시되는 것을 방지)
                    headers.cacheControl(cacheControlConfig -> {
                    });
                })

                // 4. URL 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/public/**").permitAll()
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().denyAll())

                // 5. 예외 처리 설정
                .exceptionHandling(exception -> exception
                                           .authenticationEntryPoint((request, response, authException) -> {
                                               response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                               response.setContentType("application/json;charset=UTF-8");
                                               response.getWriter().write("{\"error\": \"인증이 필요합니다.\"}");
                                           })
                                           .accessDeniedHandler((request, response, accessDeniedException) -> {
                                               response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                               response.setContentType("application/json;charset=UTF-8");
                                               response.getWriter().write("{\"error\": \"접근 권한이 없습니다.\"}");
                                           })
                                  );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}