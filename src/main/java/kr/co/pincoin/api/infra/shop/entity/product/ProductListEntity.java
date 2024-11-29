package kr.co.pincoin.api.infra.shop.entity.product;

import jakarta.persistence.*;
import kr.co.pincoin.api.infra.common.BaseDateTime;
import kr.co.pincoin.api.infra.shop.entity.store.StoreEntity;
import lombok.*;

@Entity
@Table(name = "shop_productlist")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class ProductListEntity extends BaseDateTime {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "name")
  private String name;

  @Column(name = "code")
  private String code;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "store_id")
  private StoreEntity store;
}
