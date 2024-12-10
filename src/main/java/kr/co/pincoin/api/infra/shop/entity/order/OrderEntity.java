package kr.co.pincoin.api.infra.shop.entity.order;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderCurrency;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderPaymentMethod;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderStatus;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderVisibility;
import kr.co.pincoin.api.infra.auth.entity.user.UserEntity;
import kr.co.pincoin.api.infra.common.BaseRemovalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

  @Column(name = "order_no", columnDefinition = "CHAR(32)")
  private String orderNo;

  @Column(name = "fullname")
  private String fullname;

  @Column(name = "user_agent")
  private String userAgent;

  @Column(name = "accept_language")
  private String acceptLanguage;

  @Column(name = "ip_address", columnDefinition = "CHAR(39)")
  private String ipAddress;

  @Enumerated(EnumType.ORDINAL)
  @Column(name = "payment_method", columnDefinition = "INT")
  private OrderPaymentMethod paymentMethod;

  @Column(name = "transaction_id")
  private String transactionId;

  @Enumerated(EnumType.ORDINAL)
  @Column(name = "status", columnDefinition = "INT")
  private OrderStatus status;

  @Enumerated(EnumType.ORDINAL)
  @Column(name = "visible", columnDefinition = "INT")
  private OrderVisibility visibility;

  @Column(name = "total_list_price")
  private BigDecimal totalListPrice;

  @Column(name = "total_selling_price")
  private BigDecimal totalSellingPrice;

  @Enumerated(EnumType.STRING)
  @Column(name = "currency")
  private OrderCurrency currency;

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
