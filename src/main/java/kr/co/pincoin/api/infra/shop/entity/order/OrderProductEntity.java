package kr.co.pincoin.api.infra.shop.entity.order;

import jakarta.persistence.*;
import kr.co.pincoin.api.infra.common.BaseRemovalDateTime;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "shop_orderproduct")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class OrderProductEntity extends BaseRemovalDateTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "subtitle")
    private String subtitle;

    @Column(name = "code")
    private String code;

    @Column(name = "list_price")
    private BigDecimal listPrice;

    @Column(name = "selling_price")
    private BigDecimal sellingPrice;

    @Column(name = "quantity")
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private OrderEntity order;

    public static OrderProductEntity of(String name,
                                        String subtitle,
                                        String code,
                                        BigDecimal listPrice,
                                        BigDecimal sellingPrice,
                                        Integer quantity,
                                        OrderEntity order) {
        OrderProductEntity orderProduct = OrderProductEntity.builder()
                .name(name)
                .subtitle(subtitle)
                .code(code)
                .listPrice(listPrice)
                .sellingPrice(sellingPrice)
                .quantity(quantity)
                .build();

        orderProduct.setOrder(order);
        return orderProduct;
    }

    // 연관관계 편의 메서드
    protected void setOrder(OrderEntity order) {
        this.order = order;
    }
}