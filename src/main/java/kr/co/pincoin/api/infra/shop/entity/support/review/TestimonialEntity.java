package kr.co.pincoin.api.infra.shop.entity.support.review;

import jakarta.persistence.*;
import kr.co.pincoin.api.infra.auth.entity.user.UserEntity;
import kr.co.pincoin.api.infra.common.BaseRemovalDateTime;
import kr.co.pincoin.api.infra.shop.entity.store.StoreEntity;
import lombok.*;

@Entity
@Table(name = "shop_testimonials")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class TestimonialEntity extends BaseRemovalDateTime {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "title")
  private String title;

  @Column(name = "description")
  private String description;

  @Column(name = "keywords")
  private String keywords;

  @Column(name = "content")
  private String content;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "owner_id")
  private UserEntity owner;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "store_id")
  private StoreEntity store;
}
