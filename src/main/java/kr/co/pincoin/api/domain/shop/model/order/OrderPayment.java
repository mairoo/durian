package kr.co.pincoin.api.domain.shop.model.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import kr.co.pincoin.api.domain.shop.model.order.enums.PaymentAccount;
import kr.co.pincoin.api.infra.shop.entity.order.OrderEntity;
import kr.co.pincoin.api.infra.shop.entity.order.OrderPaymentEntity;
import lombok.Builder;
import lombok.Getter;
import org.springframework.lang.Nullable;

@Getter
public class OrderPayment {

  // 핵심 식별 정보 (불변)
  private final Long id;
  private final PaymentAccount account;
  private final BigDecimal amount;
  private final BigDecimal balance;
  private final LocalDateTime received;

  // 생성/수정 시간 (불변)
  private final LocalDateTime created;
  private final LocalDateTime modified;

  // 연관 관계 (불변)
  private final Order order;

  // 상태 정보 (가변)
  private Boolean isRemoved;

  @Builder
  private OrderPayment(
      Long id,
      PaymentAccount account,
      BigDecimal amount,
      BigDecimal balance,
      LocalDateTime received,
      LocalDateTime created,
      LocalDateTime modified,
      @Nullable Order order,
      Boolean isRemoved) {
    this.id = id;
    this.account = account;
    this.amount = amount;
    this.balance = balance;
    this.received = received;
    this.created = created;
    this.modified = modified;
    this.order = order;
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
        .order(
            Optional.ofNullable(this.order)
                .map(order -> OrderEntity.builder().id(order.getId()).build())
                .orElse(null))
        .build();
  }

  public void softDelete() {
    this.isRemoved = true;
  }

  public void restore() {
    this.isRemoved = false;
  }
}
