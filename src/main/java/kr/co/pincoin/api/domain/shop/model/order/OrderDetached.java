package kr.co.pincoin.api.domain.shop.model.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderCurrency;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderPaymentMethod;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderStatus;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderVisibility;
import lombok.Getter;

@Getter
public class OrderDetached {

  // 핵심 식별 정보
  private final Long id;
  private final String orderNo;

  // 주문자 정보
  private final String fullname;
  private final String userAgent;
  private final String acceptLanguage;
  private final String ipAddress;

  // 금액 정보
  private final BigDecimal totalListPrice;
  private final BigDecimal totalSellingPrice;
  private final OrderCurrency currency;

  // 연관 ID
  private final Long parentId;
  private final Integer userId;

  // 생성/수정 시간
  private final LocalDateTime created;
  private final LocalDateTime modified;

  // 주문 상태 정보
  private final OrderPaymentMethod paymentMethod;
  private final OrderStatus status;
  private final OrderVisibility visibility;
  private final String transactionId;
  private final String message;

  // 플래그
  private final Boolean suspicious;
  private final Boolean isRemoved;

  public OrderDetached(
      Long id,
      String orderNo,
      String fullname,
      String userAgent,
      String acceptLanguage,
      String ipAddress,
      BigDecimal totalListPrice,
      BigDecimal totalSellingPrice,
      OrderCurrency currency,
      Long parentId,
      Integer userId,
      LocalDateTime created,
      LocalDateTime modified,
      OrderPaymentMethod paymentMethod,
      OrderStatus status,
      OrderVisibility visibility,
      String transactionId,
      String message,
      Boolean suspicious,
      Boolean isRemoved) {

    this.id = id;
    this.orderNo = orderNo;
    this.fullname = fullname;
    this.userAgent = userAgent;
    this.acceptLanguage = acceptLanguage;
    this.ipAddress = ipAddress;
    this.totalListPrice = totalListPrice;
    this.totalSellingPrice = totalSellingPrice;
    this.currency = currency;
    this.parentId = parentId;
    this.userId = userId;
    this.created = created;
    this.modified = modified;
    this.paymentMethod = paymentMethod;
    this.status = status;
    this.visibility = visibility;
    this.transactionId = transactionId;
    this.message = message;
    this.suspicious = suspicious;
    this.isRemoved = isRemoved;
  }
}
