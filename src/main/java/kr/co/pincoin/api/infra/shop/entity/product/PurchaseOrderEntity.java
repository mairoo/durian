package kr.co.pincoin.api.infra.shop.entity.product;

import jakarta.persistence.*;
import java.math.BigDecimal;
import kr.co.pincoin.api.infra.common.BaseRemovalDateTime;
import lombok.*;

@Entity
@Table(name = "shop_purchaseorder")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class PurchaseOrderEntity extends BaseRemovalDateTime {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "title")
  private String title;

  @Column(name = "content")
  private String content;

  @Column(name = "paid")
  private Boolean paid;

  @Column(name = "bank_account")
  private String bankAccount;

  @Column(name = "amount")
  private BigDecimal amount;
}
