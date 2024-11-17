package kr.co.pincoin.api.infra.shop.entity.support.inquiry;

import jakarta.persistence.*;
import kr.co.pincoin.api.infra.auth.entity.user.UserEntity;
import kr.co.pincoin.api.infra.common.BaseRemovalDateTime;
import kr.co.pincoin.api.infra.shop.entity.order.OrderEntity;
import kr.co.pincoin.api.infra.shop.entity.store.StoreEntity;
import lombok.*;

@Entity
@Table(name = "shop_customerquestion")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class CustomerQuestionEntity extends BaseRemovalDateTime {
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

    @Column(name = "category")
    private Integer category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private UserEntity owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private OrderEntity order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private StoreEntity store;
}
