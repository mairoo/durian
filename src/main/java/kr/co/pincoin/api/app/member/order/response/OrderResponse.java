package kr.co.pincoin.api.app.member.order.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import kr.co.pincoin.api.domain.shop.model.order.Order;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderCurrency;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderPaymentMethod;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderStatus;
import lombok.Getter;

@Getter
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

  public OrderResponse(Order order) {
    this.id = order.getId();
    this.orderNo = order.getOrderNo();
    this.fullname = order.getFullname();
    this.totalListPrice = order.getTotalListPrice();
    this.totalSellingPrice = order.getTotalSellingPrice();
    this.currency = order.getCurrency();
    this.status = order.getStatus();
    this.paymentMethod = order.getPaymentMethod();
    this.created = order.getCreated();
    this.modified = order.getModified();
  }

  // 도메인 모델 객체에서 응답 객체 초기화
  public static OrderResponse from(Order order) {
    return new OrderResponse(order);
  }
}
