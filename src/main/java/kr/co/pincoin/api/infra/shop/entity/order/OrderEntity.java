package kr.co.pincoin.api.infra.shop.entity.order;

import jakarta.persistence.*;
import kr.co.pincoin.api.infra.auth.entity.user.UserEntity;
import kr.co.pincoin.api.infra.common.BaseRemovalDateTime;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "shop_order")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class OrderEntity extends BaseRemovalDateTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "order_no", columnDefinition = "CHAR(32")
    private String orderNo;

    @Column(name = "fullname")
    private String fullname;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "accept_language")
    private String acceptLanguage;

    @Column(name = "ip_address", columnDefinition = "CHAR(39)")
    private String ipAddress;

    @Column(name = "payment_method")
    private Integer paymentMethod;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "status")
    private Integer status;

    @Column(name = "visible")
    private Integer visible;

    @Column(name = "total_list_price")
    private BigDecimal totalListPrice;

    @Column(name = "total_selling_price")
    private BigDecimal totalSellingPrice;

    @Column(name = "currency")
    private String currency;

    @Column(name = "message")
    private String message;

    @Column(name = "suspicious")
    private Boolean suspicious;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private OrderEntity parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;
}