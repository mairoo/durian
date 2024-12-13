package kr.co.pincoin.api.global.response.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import kr.co.pincoin.api.domain.shop.model.order.Order;
import kr.co.pincoin.api.domain.shop.model.order.OrderDetached;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderCurrency;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderPaymentMethod;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderResponse {
  @JsonProperty("id")
  private final Long id;

  @JsonProperty("orderNo")
  private final String orderNo;

  @JsonProperty("fullname")
  private final String fullname;

  @JsonProperty("totalListPrice")
  private final BigDecimal totalListPrice;

  @JsonProperty("totalSellingPrice")
  private final BigDecimal totalSellingPrice;

  @JsonProperty("currency")
  private final OrderCurrency currency;

  @JsonProperty("status")
  private final OrderStatus status;

  @JsonProperty("paymentMethod")
  private final OrderPaymentMethod paymentMethod;

  @JsonProperty("created")
  private final LocalDateTime created;

  @JsonProperty("modified")
  private final LocalDateTime modified;

  @JsonProperty("suspicious")
  private final Boolean suspicious;

  @JsonProperty("removed")
  private final Boolean removed;

  // 도메인 모델 객체에서 응답 객체 초기화
  public static OrderResponse from(Order order) {
    return OrderResponse.builder()
        .id(order.getId())
        .orderNo(order.getOrderNo())
        .fullname(order.getFullname())
        .totalListPrice(order.getTotalListPrice())
        .totalSellingPrice(order.getTotalSellingPrice())
        .currency(order.getCurrency())
        .status(order.getStatus())
        .paymentMethod(order.getPaymentMethod())
        .created(order.getCreated())
        .modified(order.getModified())
        .suspicious(order.getSuspicious())
        .removed(order.getRemoved())
        .build();
  }
  
  // OrderDetached 객체에서 응답 객체 초기화
  public static OrderResponse from(OrderDetached order) {
    return OrderResponse.builder()
        .id(order.getId())
        .orderNo(order.getOrderNo())
        .fullname(order.getFullname())
        .totalListPrice(order.getTotalListPrice())
        .totalSellingPrice(order.getTotalSellingPrice())
        .currency(order.getCurrency())
        .status(order.getStatus())
        .paymentMethod(order.getPaymentMethod())
        .created(order.getCreated())
        .modified(order.getModified())
        .suspicious(order.getSuspicious())
        .removed(order.getRemoved())
        .build();
  }
}
