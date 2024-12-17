package kr.co.pincoin.api.global.config;

import kr.co.pincoin.api.domain.shop.repository.order.OrderRepository;
import kr.co.pincoin.api.global.security.authorization.OrderSecurityRule;
import kr.co.pincoin.api.global.security.authorization.UserSecurityRule;
import kr.co.pincoin.api.global.security.authorization.context.OrderRequestContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@EnableMethodSecurity
public class SecurityRuleConfig {

    @Bean
    public UserSecurityRule userSecurityRule() {
        return new UserSecurityRule();
    }

    @Bean
    public OrderSecurityRule orderSecurityRule(
        OrderRepository orderRepository,
        OrderRequestContext orderRequestContext) {
        return new OrderSecurityRule(orderRepository, orderRequestContext);
    }
}