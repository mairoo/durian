package kr.co.pincoin.api.infra.shop.entity.order;

import jakarta.persistence.*;
import kr.co.pincoin.api.infra.common.BaseRemovalDateTime;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    @Column(name = "account")
    private Integer account;

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