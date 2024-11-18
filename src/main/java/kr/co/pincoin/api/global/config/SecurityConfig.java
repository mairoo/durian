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
                // CSRF 비활성화
                .csrf(AbstractHttpConfigurer::disable)

                // 세션 관리 비활성화
                .sessionManagement(session ->
                                           session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Form 로그인 비활성화
                .formLogin(AbstractHttpConfigurer::disable)

                // HTTP Basic 인증 비활성화
                .httpBasic(AbstractHttpConfigurer::disable)

                // Remember-Me 비활성화
                .rememberMe(AbstractHttpConfigurer::disable)

                // HTTP Digest 인증 비활성화 (기본적으로 비활성화됨)

                // Anonymous 인증 비활성화
                .anonymous(AbstractHttpConfigurer::disable)

                // x509 인증 비활성화 (기본적으로 비활성화됨)

                // OAuth2 비활성화 (기본적으로 비활성화됨)

                // 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/public/**").permitAll()
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().denyAll())

                // Exception Handling
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