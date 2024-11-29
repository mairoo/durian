package kr.co.pincoin.api.infra.shop.entity.order;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import kr.co.pincoin.api.domain.shop.model.order.enums.PaymentAccount;
import kr.co.pincoin.api.infra.common.BaseRemovalDateTime;
import lombok.*;

@Entity
@Table(name = "shop_orderpayment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class OrderPaymentEntity extends BaseRemovalDateTime {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Enumerated(EnumType.ORDINAL)
  @Column(name = "account", columnDefinition = "INT")
  private PaymentAccount account;

  @Column(name = "amount")
  private BigDecimal amount;

  @Column(name = "balance")
  private BigDecimal balance;

  @Column(name = "received")
  private LocalDateTime received;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id")
  private OrderEntity order;
}
