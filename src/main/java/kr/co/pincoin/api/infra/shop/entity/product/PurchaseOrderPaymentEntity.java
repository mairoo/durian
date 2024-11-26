package kr.co.pincoin.api.infra.shop.entity.product;

import jakarta.persistence.*;
import kr.co.pincoin.api.infra.common.BaseRemovalDateTime;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "shop_purchaseorderpayment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class PurchaseOrderPaymentEntity extends BaseRemovalDateTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "account")
    private Integer account;

    @Column(name = "amount")
    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private PurchaseOrderEntity order;
}
