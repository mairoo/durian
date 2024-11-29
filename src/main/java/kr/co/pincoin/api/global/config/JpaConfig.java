package kr.co.pincoin.api.global.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing // created, modified 필드 = Django 호환
@Slf4j
public class JpaConfig {}
