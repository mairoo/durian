package kr.co.pincoin.api.app.admin.order.request;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import kr.co.pincoin.api.domain.shop.model.order.OrderPayment;
import kr.co.pincoin.api.domain.shop.model.order.enums.PaymentAccount;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdminOrderPaymentRequest {

  @NotNull private PaymentAccount account;

  @NotNull private BigDecimal amount;

  @NotNull private LocalDateTime received;

  public OrderPayment toEntity() {
    return OrderPayment.builder()
        .account(account)
        .amount(amount)
        .received(received)
        .balance(BigDecimal.ZERO)
        .build();
  }
}
