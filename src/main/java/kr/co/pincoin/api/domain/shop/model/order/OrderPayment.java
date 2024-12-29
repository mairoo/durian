package kr.co.pincoin.api.domain.shop.model.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import kr.co.pincoin.api.domain.shop.model.order.enums.PaymentAccount;
import kr.co.pincoin.api.global.exception.BusinessException;
import kr.co.pincoin.api.global.exception.ErrorCode;
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

  private Order order;

  private BigDecimal balance;

  private LocalDateTime received;

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

  public void updateOrder(Order order) {
    this.order = order;
  }

  public void receive(BigDecimal balance) {
    if (this.received != null) {
      throw new BusinessException(ErrorCode.PAYMENT_ALREADY_RECEIVED);
    }

    if (balance.compareTo(BigDecimal.ZERO) < 0) {
      throw new BusinessException(ErrorCode.NEGATIVE_BALANCE);
    }

    this.received = LocalDateTime.now();
    this.balance = balance;
  }

  public void softDelete() {
    this.isRemoved = true;
  }

  public void restore() {
    this.isRemoved = false;
  }

  public boolean isReceived() {
    return this.received != null;
  }

  public boolean isPending() {
    return this.received == null;
  }

  public boolean isFullPayment() {
    return this.amount.compareTo(this.balance) == 0;
  }

  public BigDecimal getUnpaidAmount() {
    if (this.balance == null) {
      return this.amount;
    }
    return this.amount.subtract(this.balance);
  }

  public double getPaymentRate() {
    if (this.balance == null || this.amount.compareTo(BigDecimal.ZERO) == 0) {
      return 0.0;
    }
    return this.balance
        .divide(this.amount, 4, java.math.RoundingMode.HALF_UP)
        .multiply(BigDecimal.valueOf(100))
        .doubleValue();
  }
}
