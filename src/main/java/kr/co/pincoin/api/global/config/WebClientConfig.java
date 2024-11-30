package kr.co.pincoin.api.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

  @Bean
  public WebClient aligoWebClient(@Value("${aligo.base-url}") String baseUrl) {
    return WebClient.builder().baseUrl(baseUrl).build();
  }

  @Bean
  public WebClient mailgunWebClient(
      @Value("${mailgun.base-url}") String baseUrl, @Value("${mailgun.key}") String key) {
    return WebClient.builder()
        .baseUrl(baseUrl)
        .defaultHeaders(headers -> headers.setBasicAuth("api", key))
        .build();
  }
}
