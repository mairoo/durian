package kr.co.pincoin.api.domain.shop.model.product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import kr.co.pincoin.api.domain.shop.model.product.enums.ProductStatus;
import kr.co.pincoin.api.domain.shop.model.product.enums.ProductStock;
import kr.co.pincoin.api.infra.shop.entity.product.ProductEntity;
import lombok.Getter;

@Getter
public class ProductList {

    // 핵심 식별 정보
    private final Long id;
    private final String code;

    // 상품 기본 정보
    private final String name;
    private final String subtitle;
    private final Boolean pg;

    // 네이버 관련 정보
    private final Boolean naverPartner;
    private final String naverPartnerTitle;
    private final String naverPartnerTitlePg;
    private final String naverAttribute;

    // 생성/수정 시간
    private final LocalDateTime created;
    private final LocalDateTime modified;

    // 상태 및 재고 정보
    private final Long categoryId;
    private final ProductStatus status;
    private final ProductStock stock;
    private final BigDecimal listPrice;
    private final BigDecimal sellingPrice;
    private final BigDecimal pgSellingPrice;
    private final Integer minimumStockLevel;
    private final Integer maximumStockLevel;
    private final Integer stockQuantity;
    private final String description;
    private final Integer position;

    // 리뷰 카운트
    private final Integer reviewCount;
    private final Integer reviewCountPg;

    // 삭제 상태
    private final Boolean isRemoved;

    public ProductList(
        Long id,
        String code,
        String name,
        String subtitle,
        Boolean pg,
        Boolean naverPartner,
        String naverPartnerTitle,
        String naverPartnerTitlePg,
        String naverAttribute,
        LocalDateTime created,
        LocalDateTime modified,
        Long categoryId,
        ProductStatus status,
        ProductStock stock,
        BigDecimal listPrice,
        BigDecimal sellingPrice,
        BigDecimal pgSellingPrice,
        Integer minimumStockLevel,
        Integer maximumStockLevel,
        Integer stockQuantity,
        String description,
        Integer position,
        Integer reviewCount,
        Integer reviewCountPg,
        Boolean isRemoved) {

        this.id = id;
        this.code = code;
        this.name = name;
        this.subtitle = subtitle;
        this.pg = pg;
        this.naverPartner = naverPartner;
        this.naverPartnerTitle = naverPartnerTitle;
        this.naverPartnerTitlePg = naverPartnerTitlePg;
        this.naverAttribute = naverAttribute;
        this.created = created;
        this.modified = modified;
        this.categoryId = categoryId;
        this.status = status;
        this.stock = stock;
        this.listPrice = listPrice;
        this.sellingPrice = sellingPrice;
        this.pgSellingPrice = pgSellingPrice;
        this.minimumStockLevel = minimumStockLevel;
        this.maximumStockLevel = maximumStockLevel;
        this.stockQuantity = stockQuantity;
        this.description = description;
        this.position = position;
        this.reviewCount = reviewCount;
        this.reviewCountPg = reviewCountPg;
        this.isRemoved = isRemoved;
    }

    // 엔티티로부터 생성하는 팩토리 메서드
    public static ProductList from(ProductEntity entity) {
        return new ProductList(
            entity.getId(),
            entity.getCode(),
            entity.getName(),
            entity.getSubtitle(),
            entity.getPg(),
            entity.getNaverPartner(),
            entity.getNaverPartnerTitle(),
            entity.getNaverPartnerTitlePg(),
            entity.getNaverAttribute(),
            entity.getCreated(),
            entity.getModified(),
            entity.getCategory().getId(),
            entity.getStatus(),
            entity.getStock(),
            entity.getListPrice(),
            entity.getSellingPrice(),
            entity.getPgSellingPrice(),
            entity.getMinimumStockLevel(),
            entity.getMaximumStockLevel(),
            entity.getStockQuantity(),
            entity.getDescription(),
            entity.getPosition(),
            entity.getReviewCount(),
            entity.getReviewCountPg(),
            entity.isRemoved()
        );
    }
}
