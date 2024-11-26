package kr.co.pincoin.api.domain.shop.model.product;

import kr.co.pincoin.api.domain.shop.model.product.enums.ProductStatus;
import kr.co.pincoin.api.domain.shop.model.product.enums.ProductStock;
import kr.co.pincoin.api.domain.shop.model.store.Store;
import kr.co.pincoin.api.infra.shop.entity.product.ProductEntity;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Getter
public class Product {
    // 핵심 식별 정보 (불변)
    private final Long id;
    private final String code;

    // 상품 기본 정보 (불변)
    private final String name;
    private final String subtitle;
    private final BigDecimal listPrice;
    private final BigDecimal sellingPrice;
    private final Boolean pg;
    private final BigDecimal pgSellingPrice;
    private final Integer minimumStockLevel;
    private final Integer maximumStockLevel;

    // 네이버 관련 정보 (불변)
    private final Boolean naverPartner;
    private final String naverPartnerTitle;
    private final String naverPartnerTitlePg;
    private final String naverAttribute;

    // 연관 관계 (불변)
    private final Category category;
    private final Store store;

    // 생성/수정 시간 (불변)
    private final LocalDateTime created;
    private final LocalDateTime modified;

    // 상태 및 재고 정보 (가변)
    private String description;
    private Integer position;
    private final ProductStatus status;
    private Integer stockQuantity;
    private final ProductStock stock;

    // 리뷰 카운트 (가변)
    private Integer reviewCount;
    private Integer reviewCountPg;

    // 삭제 상태 (가변)
    private Boolean isRemoved;

    @Builder
    private Product(Long id,
                    String name,
                    String subtitle,
                    String code,
                    BigDecimal listPrice,
                    BigDecimal sellingPrice,
                    Boolean pg,
                    BigDecimal pgSellingPrice,
                    String description,
                    Integer position,
                    ProductStatus status,
                    Integer stockQuantity,
                    ProductStock stock,
                    Integer minimumStockLevel,
                    Integer maximumStockLevel,
                    Integer reviewCount,
                    Integer reviewCountPg,
                    Boolean naverPartner,
                    String naverPartnerTitle,
                    String naverPartnerTitlePg,
                    String naverAttribute,
                    Category category,
                    Store store,
                    LocalDateTime created,
                    LocalDateTime modified,
                    Boolean isRemoved) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.subtitle = subtitle;
        this.listPrice = listPrice;
        this.sellingPrice = sellingPrice;
        this.pg = pg;
        this.pgSellingPrice = pgSellingPrice;
        this.minimumStockLevel = minimumStockLevel;
        this.maximumStockLevel = maximumStockLevel;
        this.naverPartner = naverPartner;
        this.naverPartnerTitle = naverPartnerTitle;
        this.naverPartnerTitlePg = naverPartnerTitlePg;
        this.naverAttribute = naverAttribute;
        this.category = category;
        this.store = store;
        this.created = created;
        this.modified = modified;
        this.description = description;
        this.position = position;
        this.status = status;
        this.stockQuantity = stockQuantity;
        this.stock = stock;
        this.reviewCount = reviewCount;
        this.reviewCountPg = reviewCountPg;
        this.isRemoved = isRemoved;
    }

    // 팩토리 메소드
    public static Product of(String name,
                             String subtitle,
                             String code,
                             BigDecimal listPrice,
                             BigDecimal sellingPrice,
                             Boolean pg,
                             BigDecimal pgSellingPrice,
                             Integer minimumStockLevel,
                             Integer maximumStockLevel,
                             Category category,
                             Store store) {
        return Product.builder()
                .name(name)
                .subtitle(subtitle)
                .code(code)
                .listPrice(listPrice)
                .sellingPrice(sellingPrice)
                .pg(pg)
                .pgSellingPrice(pgSellingPrice)
                .minimumStockLevel(minimumStockLevel)
                .maximumStockLevel(maximumStockLevel)
                .category(category)
                .store(store)
                .build();
    }

    // 엔티티 변환 메소드
    public ProductEntity toEntity() {
        return ProductEntity.builder()
                .id(this.getId())
                .name(this.getName())
                .subtitle(this.getSubtitle())
                .code(this.getCode())
                .listPrice(this.getListPrice())
                .sellingPrice(this.getSellingPrice())
                .pg(this.getPg())
                .pgSellingPrice(this.getPgSellingPrice())
                .description(this.getDescription())
                .position(this.getPosition())
                .status(this.getStatus())
                .stockQuantity(this.getStockQuantity())
                .stock(this.getStock())
                .minimumStockLevel(this.getMinimumStockLevel())
                .maximumStockLevel(this.getMaximumStockLevel())
                .reviewCount(this.getReviewCount())
                .reviewCountPg(this.getReviewCountPg())
                .naverPartner(this.getNaverPartner())
                .naverPartnerTitle(this.getNaverPartnerTitle())
                .naverPartnerTitlePg(this.getNaverPartnerTitlePg())
                .naverAttribute(this.getNaverAttribute())
                .category(this.getCategory().toEntity())
                .store(this.getStore().toEntity())
                .build();
    }

    // 비즈니스 계산 메소드
    public ProductStock calculateStockStatus() {
        if (stockQuantity == 0) {
            return ProductStock.SOLD_OUT;
        }
        return this.stock;
    }

    public BigDecimal getDiscountAmount() {
        return this.listPrice.subtract(this.sellingPrice);
    }

    public double getDiscountRate() {
        if (this.listPrice.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        return this.getDiscountAmount()
                .divide(this.listPrice, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }

    // 상태 변경 메소드
    public void updateStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updatePosition(Integer position) {
        this.position = position;
    }

    public void incrementReviewCount(boolean isPgPurchase) {
        if (isPgPurchase) {
            this.reviewCountPg++;
        }
        this.reviewCount++;
    }

    public void softDelete() {
        this.isRemoved = true;
    }

    public void restore() {
        this.isRemoved = false;
    }
}