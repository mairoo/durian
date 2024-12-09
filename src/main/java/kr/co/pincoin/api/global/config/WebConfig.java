package kr.co.pincoin.api.global.config;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Slf4j
public class WebConfig implements WebMvcConfigurer {
  // Rest API 서버에서는 별도의 Web MVC 설정을 할 필요 없다.
  // 단, CORS 설정, 인터셉터, 메시지 컨버터가 필요할 경우 설정이 필요 - 최소한의 설정

  @Value("${web-config.cors.allowed-origins:*}")
  private String allowedOrigins;

  @Value("${web-config.cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS}")
  private String allowedMethods;

  @Value("${web-config.cors.allowed-headers:*}")
  private String allowedHeaders;

  @Value("${web-config.cors.max-age:3600}")
  private long maxAge;

  /** CORS 설정 WebConfig에서 CORS를 설정했다면 일반적으로 @CrossOrigin 애노테이션은 불필요 특별한 경우에만 컨트롤러에 선택적으로 사용 가능 */
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry
        .addMapping("/**")
        .allowedOriginPatterns(allowedOrigins.split(","))
        .allowedMethods(allowedMethods.split(","))
        .allowedHeaders(allowedHeaders.split(","))
        .allowCredentials(true)
        .maxAge(maxAge);
  }

  /** CORS 설정 소스 빈 스프링 시큐리티가 아니라 Web MVC 설정에서 빈 컴포넌트 클래스 정의 */
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOriginPatterns(List.of(allowedOrigins.split(",")));
    configuration.setAllowedMethods(List.of(allowedMethods.split(",")));
    configuration.setAllowedHeaders(List.of(allowedHeaders.split(",")));
    configuration.setAllowCredentials(true);
    configuration.setExposedHeaders(List.of("Set-Cookie"));
    configuration.setMaxAge(maxAge);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    SortHandlerMethodArgumentResolver sortResolver = new SortHandlerMethodArgumentResolver();
    sortResolver.setFallbackSort(Sort.by(Sort.Order.desc("id")));

    PageableHandlerMethodArgumentResolver pageResolver =
        new PageableHandlerMethodArgumentResolver(sortResolver);
    pageResolver.setFallbackPageable(PageRequest.of(0, 20));

    resolvers.add(pageResolver);
  }
}
