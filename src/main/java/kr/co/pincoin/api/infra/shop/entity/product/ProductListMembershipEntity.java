package kr.co.pincoin.api.infra.shop.entity.product;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "shop_productlistmembership")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class ProductListMembershipEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "position")
  private Integer position;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id")
  private ProductEntity product;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_list_id")
  private ProductListEntity productList;
}
