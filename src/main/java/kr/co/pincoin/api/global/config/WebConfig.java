package kr.co.pincoin.api.global.config;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
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
}
