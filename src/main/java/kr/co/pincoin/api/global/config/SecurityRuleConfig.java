package kr.co.pincoin.api.global.config;

import kr.co.pincoin.api.domain.shop.repository.order.OrderRepository;
import kr.co.pincoin.api.global.security.authorization.OrderSecurityRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@EnableMethodSecurity
public class SecurityRuleConfig {

    @Bean
    public OrderSecurityRule orderSecurityRule(OrderRepository orderRepository) {
        return new OrderSecurityRule(orderRepository);
    }
}