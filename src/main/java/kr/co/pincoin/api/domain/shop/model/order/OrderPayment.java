package kr.co.pincoin.api.domain.shop.model.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import kr.co.pincoin.api.domain.shop.model.order.enums.PaymentAccount;
import kr.co.pincoin.api.infra.shop.entity.order.OrderPaymentEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderPayment {
  private final Long id;

  private final PaymentAccount account;

  private final BigDecimal amount;

  private final LocalDateTime created;

  private final LocalDateTime modified;

  private final Order order;

  private final BigDecimal balance;

  private final LocalDateTime received;

  private Boolean isRemoved;

  @Builder
  private OrderPayment(
      Long id,
      PaymentAccount account,
      BigDecimal amount,
      BigDecimal balance,
      LocalDateTime received,
      Order order,
      LocalDateTime created,
      LocalDateTime modified,
      Boolean isRemoved) {
    this.id = id;
    this.account = account;
    this.amount = amount;
    this.balance = balance;
    this.received = received;
    this.order = order;
    this.created = created;
    this.modified = modified;
    this.isRemoved = isRemoved;
  }

  public static OrderPayment of(Order order, PaymentAccount account, BigDecimal amount) {
    return OrderPayment.builder().order(order).account(account).amount(amount).build();
  }

  public OrderPaymentEntity toEntity() {
    return OrderPaymentEntity.builder()
        .id(this.getId())
        .account(this.getAccount())
        .amount(this.getAmount())
        .balance(this.getBalance())
        .received(this.getReceived())
        .order(this.getOrder().toEntity())
        .build();
  }

  public void softDelete() {
    this.isRemoved = true;
  }

  public void restore() {
    this.isRemoved = false;
  }
}
