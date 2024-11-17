package kr.co.pincoin.api.domain.shop.model.product;

import kr.co.pincoin.api.domain.shop.model.store.Store;
import kr.co.pincoin.api.infra.shop.entity.product.ProductEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Getter
public class Product {
    private final Long id;
    private final String name;
    private final String subtitle;
    private final String code;
    private final BigDecimal listPrice;
    private final BigDecimal sellingPrice;
    private final Boolean pg;
    private final BigDecimal pgSellingPrice;
    private final Integer minimumStockLevel;
    private final Integer maximumStockLevel;
    private final Boolean naverPartner;
    private final String naverPartnerTitle;
    private final String naverPartnerTitlePg;
    private final String naverAttribute;
    private final Category category;
    private final Store store;
    private final LocalDateTime created;
    private final LocalDateTime modified;
    private String description;
    private Integer position;
    private Integer status;
    private Integer stockQuantity;
    private Integer stock;
    private Integer reviewCount;
    private Integer reviewCountPg;
    private Boolean isRemoved;

    @Builder(access = AccessLevel.PRIVATE, builderMethodName = "instanceBuilder")
    private Product(String name, String subtitle, String code,
                    BigDecimal listPrice, BigDecimal sellingPrice,
                    Boolean pg, BigDecimal pgSellingPrice, String description,
                    Integer position, Integer minimumStockLevel, Integer maximumStockLevel,
                    Boolean naverPartner, String naverPartnerTitle,
                    String naverPartnerTitlePg, String naverAttribute,
                    Category category, Store store) {
        this.id = null;
        this.name = name;
        this.subtitle = subtitle;
        this.code = code;
        this.listPrice = listPrice;
        this.sellingPrice = sellingPrice;
        this.pg = pg;
        this.pgSellingPrice = pgSellingPrice;
        this.description = description;
        this.position = position;
        this.status = ProductStatus.DRAFT.getValue();
        this.stockQuantity = 0;
        this.stock = 0;
        this.minimumStockLevel = minimumStockLevel;
        this.maximumStockLevel = maximumStockLevel;
        this.reviewCount = 0;
        this.reviewCountPg = 0;
        this.naverPartner = naverPartner;
        this.naverPartnerTitle = naverPartnerTitle;
        this.naverPartnerTitlePg = naverPartnerTitlePg;
        this.naverAttribute = naverAttribute;
        this.category = category;
        this.store = store;
        this.created = LocalDateTime.now();
        this.modified = LocalDateTime.now();
        this.isRemoved = false;

        validatePrices();
        validateStockLevels();
    }

    @Builder(access = AccessLevel.PRIVATE, builderMethodName = "jpaBuilder")
    private Product(Long id, String name, String subtitle, String code,
                    BigDecimal listPrice, BigDecimal sellingPrice,
                    Boolean pg, BigDecimal pgSellingPrice, String description,
                    Integer position, Integer status, Integer stockQuantity,
                    Integer stock, Integer minimumStockLevel, Integer maximumStockLevel,
                    Integer reviewCount, Integer reviewCountPg,
                    Boolean naverPartner, String naverPartnerTitle,
                    String naverPartnerTitlePg, String naverAttribute,
                    Category category, Store store,
                    LocalDateTime created, LocalDateTime modified, Boolean isRemoved) {
        this.id = id;
        this.name = name;
        this.subtitle = subtitle;
        this.code = code;
        this.listPrice = listPrice;
        this.sellingPrice = sellingPrice;
        this.pg = pg;
        this.pgSellingPrice = pgSellingPrice;
        this.description = description;
        this.position = position;
        this.status = status;
        this.stockQuantity = stockQuantity;
        this.stock = stock;
        this.minimumStockLevel = minimumStockLevel;
        this.maximumStockLevel = maximumStockLevel;
        this.reviewCount = reviewCount;
        this.reviewCountPg = reviewCountPg;
        this.naverPartner = naverPartner;
        this.naverPartnerTitle = naverPartnerTitle;
        this.naverPartnerTitlePg = naverPartnerTitlePg;
        this.naverAttribute = naverAttribute;
        this.category = category;
        this.store = store;
        this.created = created;
        this.modified = modified;
        this.isRemoved = isRemoved;

        validatePrices();
        validateStockLevels();
    }

    public static Product of(String name, String subtitle, String code,
                             BigDecimal listPrice, BigDecimal sellingPrice,
                             Boolean pg, BigDecimal pgSellingPrice,
                             Integer minimumStockLevel, Integer maximumStockLevel,
                             Category category, Store store) {
        return Product.instanceBuilder()
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

    public static Product from(ProductEntity entity) {
        return Product.jpaBuilder()
                .id(entity.getId())
                .name(entity.getName())
                .subtitle(entity.getSubtitle())
                .code(entity.getCode())
                .listPrice(entity.getListPrice())
                .sellingPrice(entity.getSellingPrice())
                .pg(entity.getPg())
                .pgSellingPrice(entity.getPgSellingPrice())
                .description(entity.getDescription())
                .position(entity.getPosition())
                .status(entity.getStatus())
                .stockQuantity(entity.getStockQuantity())
                .stock(entity.getStock())
                .minimumStockLevel(entity.getMinimumStockLevel())
                .maximumStockLevel(entity.getMaximumStockLevel())
                .reviewCount(entity.getReviewCount())
                .reviewCountPg(entity.getReviewCountPg())
                .naverPartner(entity.getNaverPartner())
                .naverPartnerTitle(entity.getNaverPartnerTitle())
                .naverPartnerTitlePg(entity.getNaverPartnerTitlePg())
                .naverAttribute(entity.getNaverAttribute())
                .category(Category.from(entity.getCategory()))
                .store(Store.from(entity.getStore()))
                .created(entity.getCreated())
                .modified(entity.getModified())
                .isRemoved(entity.getIsRemoved())
                .build();
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updatePosition(Integer position) {
        this.position = position;
    }

    public void publish() {
        if (this.status.equals(ProductStatus.PUBLISHED.getValue())) {
            throw new IllegalStateException("Product is already published");
        }
        validateForPublication();
        this.status = ProductStatus.PUBLISHED.getValue();
    }

    public void unpublish() {
        this.status = ProductStatus.UNPUBLISHED.getValue();
    }

    public void addStock(Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Stock addition quantity must be positive");
        }
        if (this.stock + quantity > this.maximumStockLevel) {
            throw new IllegalStateException("Adding stock would exceed maximum stock level");
        }
        this.stock += quantity;
        this.stockQuantity += quantity;
    }

    public void reduceStock(Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Stock reduction quantity must be positive");
        }
        if (this.stock < quantity) {
            throw new IllegalStateException("Insufficient stock");
        }
        this.stock -= quantity;
    }

    public void incrementReviewCount(boolean isPgPurchase) {
        if (isPgPurchase) {
            this.reviewCountPg++;
        }
        this.reviewCount++;
    }

    public void remove() {
        if (isPublished()) {
            throw new IllegalStateException("Cannot remove published product");
        }
        this.isRemoved = true;
    }

    public void restore() {
        this.isRemoved = false;
    }

    public boolean isPublished() {
        return ProductStatus.PUBLISHED.getValue().equals(this.status);
    }

    public boolean isDraft() {
        return ProductStatus.DRAFT.getValue().equals(this.status);
    }

    public boolean isUnpublished() {
        return ProductStatus.UNPUBLISHED.getValue().equals(this.status);
    }

    public boolean isLowStock() {
        return this.stock <= this.minimumStockLevel;
    }

    public boolean isOutOfStock() {
        return this.stock <= 0;
    }

    public boolean hasStock(Integer quantity) {
        return this.stock >= quantity;
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

    private void validatePrices() {
        if (listPrice == null || listPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("List price must be non-negative");
        }
        if (sellingPrice == null || sellingPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Selling price must be non-negative");
        }
        if (sellingPrice.compareTo(listPrice) > 0) {
            throw new IllegalArgumentException("Selling price cannot exceed list price");
        }
        if (pg && (pgSellingPrice == null || pgSellingPrice.compareTo(BigDecimal.ZERO) < 0)) {
            throw new IllegalArgumentException("PG selling price must be non-negative for PG products");
        }
    }

    private void validateStockLevels() {
        if (minimumStockLevel == null || minimumStockLevel < 0) {
            throw new IllegalArgumentException("Minimum stock level must be non-negative");
        }
        if (maximumStockLevel == null || maximumStockLevel <= 0) {
            throw new IllegalArgumentException("Maximum stock level must be positive");
        }
        if (minimumStockLevel >= maximumStockLevel) {
            throw new IllegalArgumentException("Minimum stock level must be less than maximum stock level");
        }
    }

    private void validateForPublication() {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalStateException("Description is required for publication");
        }
        if (stock <= minimumStockLevel) {
            throw new IllegalStateException("Stock level is too low for publication");
        }
    }

    public enum ProductStatus {
        DRAFT(0),
        PUBLISHED(1),
        UNPUBLISHED(2);

        private final Integer value;

        ProductStatus(Integer value) {
            this.value = value;
        }

        public Integer getValue() {
            return value;
        }
    }
}
