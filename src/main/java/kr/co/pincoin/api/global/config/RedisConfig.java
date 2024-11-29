package kr.co.pincoin.api.global.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableAutoConfiguration(exclude = {RedisRepositoriesAutoConfiguration.class}) // RedisRepository 끄기
@Slf4j
public class RedisConfig {
  @Bean
  public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
    // RedisRepository - 단순한 작업 처리에 편리
    // RedisTemplate - 다양한 데이터 타입 지원으로 복잡한 연산, 트랜잭션 처리, 캐시, 세션 관리 등 성능 최적화 가능
    RedisTemplate<String, Object> template = new RedisTemplate<>();

    // 직렬화 - 단순 String 문자열 입출력
    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new StringRedisSerializer());

    template.setConnectionFactory(connectionFactory);

    return template;
  }
}
