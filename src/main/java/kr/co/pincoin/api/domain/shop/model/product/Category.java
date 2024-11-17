package kr.co.pincoin.api.domain.shop.model.product;

import kr.co.pincoin.api.domain.shop.model.store.Store;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class Category {
    private final Long id;
    private final String title;
    private final String slug;
    private final Boolean pg;
    private final Category parent;
    private final Store store;
    private final LocalDateTime created;
    private final LocalDateTime modified;
    private String thumbnail;
    private String description;
    private String description1;
    private BigDecimal discountRate;
    private BigDecimal pgDiscountRate;
    private String naverSearchTag;
    private String naverBrandName;
    private String naverMakerName;
    private Integer lft;
    private Integer rght;
    private Integer treeId;
    private Integer level;

    @Builder
    private Category(Long id,
                     String title,
                     String slug,
                     String thumbnail,
                     String description,
                     String description1,
                     BigDecimal discountRate,
                     Boolean pg,
                     BigDecimal pgDiscountRate,
                     String naverSearchTag,
                     String naverBrandName,
                     String naverMakerName,
                     Integer lft,
                     Integer rght,
                     Integer treeId,
                     Integer level,
                     Category parent,
                     Store store,
                     LocalDateTime created,
                     LocalDateTime modified) {
        this.id = id;
        this.title = title;
        this.slug = slug;
        this.thumbnail = thumbnail;
        this.description = description;
        this.description1 = description1;
        this.discountRate = discountRate;
        this.pg = pg;
        this.pgDiscountRate = pgDiscountRate;
        this.naverSearchTag = naverSearchTag;
        this.naverBrandName = naverBrandName;
        this.naverMakerName = naverMakerName;
        this.lft = lft;
        this.rght = rght;
        this.treeId = treeId;
        this.level = level;
        this.parent = parent;
        this.store = store;
        this.created = created;
        this.modified = modified;

        validateCategory();
    }

    public static Category of(String title, String slug,
                              BigDecimal discountRate, Boolean pg,
                              BigDecimal pgDiscountRate,
                              Category parent, Store store) {
        return Category.builder()
                .title(title)
                .slug(slug)
                .discountRate(discountRate)
                .pg(pg)
                .pgDiscountRate(pgDiscountRate)
                .parent(parent)
                .store(store)
                .build();
    }

    public void updateNestedSetInfo(Integer lft, Integer rght, Integer treeId) {
        if (lft >= rght) {
            throw new IllegalArgumentException("Left value must be less than right value");
        }
        this.lft = lft;
        this.rght = rght;
        this.treeId = treeId;
    }

    public void updateThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void updateDescriptions(String description, String description1) {
        this.description = description;
        this.description1 = description1;
    }

    public void updateDiscountRates(BigDecimal discountRate, BigDecimal pgDiscountRate) {
        validateDiscountRates(discountRate, pgDiscountRate);
        this.discountRate = discountRate;
        this.pgDiscountRate = pgDiscountRate;
    }

    public void updateNaverInfo(String searchTag, String brandName, String makerName) {
        this.naverSearchTag = searchTag;
        this.naverBrandName = brandName;
        this.naverMakerName = makerName;
    }

    public boolean isRoot() {
        return this.parent == null;
    }

    public boolean isLeaf() {
        return this.rght - this.lft == 1;
    }

    public boolean hasChildren() {
        return !isLeaf();
    }

    public boolean isDescendantOf(Category ancestor) {
        return this.lft > ancestor.getLft() &&
                this.rght < ancestor.getRght() &&
                this.treeId.equals(ancestor.getTreeId());
    }

    public boolean isAncestorOf(Category descendant) {
        return this.lft < descendant.getLft() &&
                this.rght > descendant.getRght() &&
                this.treeId.equals(descendant.getTreeId());
    }

    public int getDescendantCount() {
        return (this.rght - this.lft - 1) / 2;
    }

    public BigDecimal calculateDiscountedPrice(BigDecimal originalPrice, boolean isPgPayment) {
        if (originalPrice == null || originalPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Original price must be positive");
        }

        BigDecimal rate = isPgPayment && this.pg ? this.pgDiscountRate : this.discountRate;
        if (rate == null || rate.compareTo(BigDecimal.ZERO) <= 0) {
            return originalPrice;
        }

        return originalPrice.multiply(BigDecimal.ONE.subtract(
                rate.divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP)
                                                             ));
    }

    private void validateCategory() {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Category title cannot be empty");
        }
        if (slug == null || slug.trim().isEmpty()) {
            throw new IllegalArgumentException("Category slug cannot be empty");
        }
        if (store == null) {
            throw new IllegalArgumentException("Store is required");
        }
        validateDiscountRates(discountRate, pgDiscountRate);
    }

    private void validateDiscountRates(BigDecimal discountRate, BigDecimal pgDiscountRate) {
        if (discountRate != null &&
                (discountRate.compareTo(BigDecimal.ZERO) < 0 ||
                        discountRate.compareTo(BigDecimal.valueOf(100)) > 0)) {
            throw new IllegalArgumentException("Discount rate must be between 0 and 100");
        }
        if (pg && pgDiscountRate != null &&
                (pgDiscountRate.compareTo(BigDecimal.ZERO) < 0 ||
                        pgDiscountRate.compareTo(BigDecimal.valueOf(100)) > 0)) {
            throw new IllegalArgumentException("PG discount rate must be between 0 and 100");
        }
    }
}
