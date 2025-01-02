package kr.co.pincoin.api.app.admin.order.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import kr.co.pincoin.api.app.member.order.response.OrderResponse;
import kr.co.pincoin.api.domain.shop.model.order.Order;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderVisibility;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminOrderResponse extends OrderResponse {

  @JsonProperty("userAgent")
  private final String userAgent;

  @JsonProperty("acceptLanguage")
  private final String acceptLanguage;

  @JsonProperty("ipAddress")
  private final String ipAddress;

  @JsonProperty("visibility")
  private OrderVisibility visibility;

  @JsonProperty("transactionId")
  private String transactionId;

  @JsonProperty("message")
  private String message;

  @JsonProperty("suspicious")
  private final Boolean suspicious;

  @JsonProperty("isRemoved")
  private final Boolean isRemoved;

  public AdminOrderResponse(Order order) {
    super(order);

    this.userAgent = order.getUserAgent();
    this.acceptLanguage = order.getAcceptLanguage();
    this.ipAddress = order.getIpAddress();
    this.visibility = order.getVisibility();
    this.transactionId = order.getTransactionId();
    this.message = order.getMessage();
    this.suspicious = order.isSuspicious();
    this.isRemoved = order.isRemoved();
  }

  public static AdminOrderResponse from(Order order) {
    return new AdminOrderResponse(order);
  }
}
