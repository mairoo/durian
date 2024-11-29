package kr.co.pincoin.api.global.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class QueryDslConfig {
  @PersistenceContext private final EntityManager em;

  @Bean
  public JPAQueryFactory jpaQueryFactory() {
    return new JPAQueryFactory(em);
  }
}
