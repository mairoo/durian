package kr.co.pincoin.api.global.security.authorization.context;

import kr.co.pincoin.api.global.exception.BusinessException;
import kr.co.pincoin.api.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
@RequiredArgsConstructor
@Setter
public class OrderRequestContext {

  private Long orderId;

  public Long getOrderId() {
    if (orderId == null) {
      throw new BusinessException(ErrorCode.ORDER_NOT_FOUND);
    }
    return orderId;
  }
}
