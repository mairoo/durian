package kr.co.pincoin.api.app.admin.order.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import kr.co.pincoin.api.app.member.order.response.OrderPaymentResponse;
import kr.co.pincoin.api.domain.shop.model.order.OrderPaymentDetached;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminOrderPaymentResponse extends OrderPaymentResponse {

  @JsonProperty("created")
  private final LocalDateTime created;

  @JsonProperty("modified")
  private final LocalDateTime modified;

  @JsonProperty("balance")
  private BigDecimal balance;

  @JsonProperty("isRemoved")
  private Boolean isRemoved;

  protected AdminOrderPaymentResponse(OrderPaymentDetached orderPayment) {
    super(orderPayment);

    this.created = orderPayment.getCreated();
    this.modified = orderPayment.getModified();
    this.balance = orderPayment.getBalance();
    this.isRemoved = orderPayment.getIsRemoved();
  }

  public static AdminOrderPaymentResponse from(OrderPaymentDetached orderPayment) {
    return new AdminOrderPaymentResponse(orderPayment);
  }
}
