package kr.co.pincoin.api.domain.shop.model.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import kr.co.pincoin.api.domain.shop.model.order.enums.PaymentAccount;
import lombok.Getter;

@Getter
public class OrderPaymentDetached {
  private final Long id;
  private final PaymentAccount account;
  private final BigDecimal amount;
  private final BigDecimal balance;
  private final LocalDateTime received;
  private final Long orderId;
  private final String orderNo;
  private final LocalDateTime created;
  private final LocalDateTime modified;
  private final Boolean isRemoved;

  public OrderPaymentDetached(
      Long id,
      PaymentAccount account,
      BigDecimal amount,
      BigDecimal balance,
      LocalDateTime received,
      Long orderId,
      String orderNo,
      LocalDateTime created,
      LocalDateTime modified,
      Boolean isRemoved) {
    this.id = id;
    this.account = account;
    this.amount = amount;
    this.balance = balance;
    this.received = received;
    this.orderId = orderId;
    this.orderNo = orderNo;
    this.created = created;
    this.modified = modified;
    this.isRemoved = isRemoved;
  }
}
