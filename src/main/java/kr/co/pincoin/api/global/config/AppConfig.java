package kr.co.pincoin.api.global.config;

import kr.co.pincoin.api.global.security.jwt.JwtProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
// @ConfigurationPropertiesScan(basePackages = "kr.co.pincoin.api")
@EnableScheduling
public class AppConfig {
    // 비동기 처리를 위한 Thread Pool 설정

    // ObjectMapper 커스터마이징

    // RestTemplate 설정

    // 캐시 설정

    // 시스템 환경 정보 로깅

    // 날짜 포맷 설정

    // 메시지 소스 설정 (다국어 지원)

    // 유틸리티 빈 등록

    // 프로파일 관련 설정
}
