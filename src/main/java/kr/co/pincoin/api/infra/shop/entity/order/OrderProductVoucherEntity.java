package kr.co.pincoin.api.infra.shop.entity.order;

import jakarta.persistence.*;
import kr.co.pincoin.api.infra.common.BaseRemovalDateTime;
import kr.co.pincoin.api.infra.shop.entity.product.VoucherEntity;
import lombok.*;

@Entity
@Table(name = "shop_orderproductvoucher")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class OrderProductVoucherEntity extends BaseRemovalDateTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "code")
    private String code;

    @Column(name = "revoked")
    private Boolean revoked;

    @Column(name = "remarks")
    private String remarks;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_product_id")
    private OrderProductEntity orderProduct;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_id")
    private VoucherEntity voucher;
}
