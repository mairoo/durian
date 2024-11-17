package kr.co.pincoin.api.infra.shop.entity.product;

import jakarta.persistence.*;
import kr.co.pincoin.api.infra.common.BaseDateTime;
import kr.co.pincoin.api.infra.shop.entity.store.StoreEntity;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "shop_category")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class CategoryEntity extends BaseDateTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "slug")
    private String slug;

    @Column(name = "thumbnail")
    private String thumbnail;

    @Column(name = "description")
    private String description;

    @Column(name = "description1")
    private String description1;

    @Column(name = "discount_rate")
    private BigDecimal discountRate;

    @Column(name = "pg")
    private Boolean pg;

    @Column(name = "pg_discount_rate")
    private BigDecimal pgDiscountRate;

    @Column(name = "naver_search_tag")
    private String naverSearchTag;

    @Column(name = "naver_brand_name")
    private String naverBrandName;

    @Column(name = "naver_maker_name")
    private String naverMakerName;

    @Column(name = "lft")
    private Integer lft;

    @Column(name = "rght")
    private Integer rght;

    @Column(name = "tree_id")
    private Integer treeId;

    @Column(name = "level")
    private Integer level;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private CategoryEntity parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private StoreEntity store;
}