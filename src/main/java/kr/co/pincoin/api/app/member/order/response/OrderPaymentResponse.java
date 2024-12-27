package kr.co.pincoin.api.app.member.order.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import kr.co.pincoin.api.domain.shop.model.order.OrderPaymentDetached;
import kr.co.pincoin.api.domain.shop.model.order.enums.PaymentAccount;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderPaymentResponse {

  @JsonProperty("id")
  private final Long id;

  @JsonProperty("paymentAccount")
  private final PaymentAccount account;

  @JsonProperty("amount")
  private final BigDecimal amount;

  @JsonProperty("orderNo")
  private final String orderNo;

  @JsonProperty("received")
  private final LocalDateTime received;

  protected OrderPaymentResponse(OrderPaymentDetached orderPayment) {
    this.id = orderPayment.getId();
    this.account = orderPayment.getAccount();
    this.amount = orderPayment.getAmount();
    this.orderNo = orderPayment.getOrderNo();
    this.received = orderPayment.getReceived();
  }

  public static OrderPaymentResponse from(OrderPaymentDetached orderPayment) {
    return new OrderPaymentResponse(orderPayment);
  }
}
