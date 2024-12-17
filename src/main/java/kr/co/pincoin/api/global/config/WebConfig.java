package kr.co.pincoin.api.global.config;

import java.util.List;
import kr.co.pincoin.api.global.converter.PaymentAccountConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Slf4j
public class WebConfig implements WebMvcConfigurer {
  // Rest API 서버에서는 별도의 Web MVC 설정을 할 필요 없다.
  // 단, 인터셉터, 메시지 컨버터가 필요할 경우 설정이 필요 - 최소한의 설정

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    SortHandlerMethodArgumentResolver sortResolver = new SortHandlerMethodArgumentResolver();
    sortResolver.setFallbackSort(Sort.by(Sort.Order.desc("id")));

    PageableHandlerMethodArgumentResolver pageResolver =
        new PageableHandlerMethodArgumentResolver(sortResolver);
    pageResolver.setFallbackPageable(PageRequest.of(0, 20));

    resolvers.add(pageResolver);
  }

  // @Component 애노테이션만 컨버터는 동작 가능
  // 스프링의 타입 변환 시스템에 명시적으로 등록하는 것을 권장
  // WebConfg에서 명시적으로 등록하면 다른 개발자들이 컨버터 존재를 쉽게 파악할 수 있고 관리가 더 쉬움
  @Override
  public void addFormatters(FormatterRegistry registry) {
    registry.addConverter(new PaymentAccountConverter());
  }
}
