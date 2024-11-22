package kr.co.pincoin.api.infra.shop.entity.product;

import jakarta.persistence.*;
import kr.co.pincoin.api.domain.shop.model.product.enums.ProductStatus;
import kr.co.pincoin.api.domain.shop.model.product.enums.ProductStock;
import kr.co.pincoin.api.infra.common.BaseRemovalDateTime;
import kr.co.pincoin.api.infra.shop.entity.store.StoreEntity;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "shop_product")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class ProductEntity extends BaseRemovalDateTime {
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

    @Column(name = "pg")
    private Boolean pg;

    @Column(name = "pg_selling_price")
    private BigDecimal pgSellingPrice;

    @Column(name = "description")
    private String description;

    @Column(name = "position")
    private Integer position;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status", columnDefinition = "INT")
    private ProductStatus status;

    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "stock", columnDefinition = "INT")
    private ProductStock stock;

    @Column(name = "minimum_stock_level")
    private Integer minimumStockLevel;

    @Column(name = "maximum_stock_level")
    private Integer maximumStockLevel;

    @Column(name = "review_count")
    private Integer reviewCount;

    @Column(name = "review_count_pg")
    private Integer reviewCountPg;

    @Column(name = "naver_partner")
    private Boolean naverPartner;

    @Column(name = "naver_partner_title")
    private String naverPartnerTitle;

    @Column(name = "naver_partner_title_pg")
    private String naverPartnerTitlePg;

    @Column(name = "naver_attribute")
    private String naverAttribute;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private CategoryEntity category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private StoreEntity store;
}