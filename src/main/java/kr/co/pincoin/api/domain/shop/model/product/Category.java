package kr.co.pincoin.api.domain.shop.model.product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import kr.co.pincoin.api.domain.shop.model.store.Store;
import kr.co.pincoin.api.global.exception.BusinessException;
import kr.co.pincoin.api.global.exception.ErrorCode;
import kr.co.pincoin.api.infra.shop.entity.product.CategoryEntity;
import kr.co.pincoin.api.infra.shop.entity.store.StoreEntity;
import lombok.Builder;
import lombok.Getter;
import org.springframework.lang.Nullable;

@Getter
public class Category {
  // 핵심 식별 정보 (불변)
  private final Long id;
  private final String title;
  private final String slug;

  // 결제 관련 정보 (불변)
  private final Boolean pg;

  // 연관 관계 (불변)
  private final Category parent;
  private final Store store;

  // 생성/수정 시간 (불변)
  private final LocalDateTime created;
  private final LocalDateTime modified;

  // 이미지 및 설명 (가변)
  private String thumbnail;
  private String description;
  private String description1;

  // 할인율 정보 (가변)
  private BigDecimal discountRate;
  private BigDecimal pgDiscountRate;

  // 네이버 관련 정보 (가변)
  private String naverSearchTag;
  private String naverBrandName;
  private String naverMakerName;

  // Nested Set Model 정보 (가변)
  private Integer lft;
  private Integer rght;
  private Integer treeId;
  private final Integer level;

  @Builder
  private Category(
      Long id,
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
      @Nullable Category parent,
      Store store,
      LocalDateTime created,
      LocalDateTime modified) {
    this.id = id;
    this.title = title;
    this.slug = slug;
    this.pg = pg;
    this.parent = parent;
    this.store = store;
    this.created = created;
    this.modified = modified;

    this.thumbnail = thumbnail;
    this.description = description;
    this.description1 = description1;
    this.discountRate = discountRate;
    this.pgDiscountRate = pgDiscountRate;
    this.naverSearchTag = naverSearchTag;
    this.naverBrandName = naverBrandName;
    this.naverMakerName = naverMakerName;
    this.lft = lft;
    this.rght = rght;
    this.treeId = treeId;
    this.level = level;
  }

  // 팩토리 메소드
  public static Category of(
      String title,
      String slug,
      BigDecimal discountRate,
      Boolean pg,
      BigDecimal pgDiscountRate,
      Category parent,
      Store store) {
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

  // 엔티티 변환 메소드
  public CategoryEntity toEntity() {
    return CategoryEntity.builder()
        .id(this.getId())
        .title(this.getTitle())
        .slug(this.getSlug())
        .thumbnail(this.getThumbnail())
        .description(this.getDescription())
        .description1(this.getDescription1())
        .discountRate(this.getDiscountRate())
        .pg(this.getPg())
        .pgDiscountRate(this.getPgDiscountRate())
        .naverSearchTag(this.getNaverSearchTag())
        .naverBrandName(this.getNaverBrandName())
        .naverMakerName(this.getNaverMakerName())
        .lft(this.getLft())
        .rght(this.getRght())
        .treeId(this.getTreeId())
        .level(this.getLevel())
        .store(StoreEntity.builder().id(1L).build())
        .parent(
            Optional.ofNullable(this.parent)
                .map(parent -> CategoryEntity.builder().id(parent.getId()).build())
                .orElse(null))
        .build();
  }

  // 트리 구조 관련 메소드
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
    return this.lft > ancestor.getLft()
        && this.rght < ancestor.getRght()
        && this.treeId.equals(ancestor.getTreeId());
  }

  public boolean isAncestorOf(Category descendant) {
    return this.lft < descendant.getLft()
        && this.rght > descendant.getRght()
        && this.treeId.equals(descendant.getTreeId());
  }

  public int getDescendantCount() {
    return (this.rght - this.lft - 1) / 2;
  }

  // 비즈니스 계산 메소드
  public BigDecimal calculateDiscountedPrice(BigDecimal originalPrice, boolean isPgPayment) {
    if (originalPrice == null || originalPrice.compareTo(BigDecimal.ZERO) <= 0) {
      throw new BusinessException(ErrorCode.INVALID_ORIGINAL_PRICE);
    }

    BigDecimal rate = isPgPayment && this.pg ? this.pgDiscountRate : this.discountRate;
    if (rate == null || rate.compareTo(BigDecimal.ZERO) <= 0) {
      return originalPrice;
    }

    return originalPrice.multiply(
        BigDecimal.ONE.subtract(
            rate.divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP)));
  }

  // 상태 변경 메소드
  public void updateNestedSetInfo(Integer lft, Integer rght, Integer treeId) {
    if (lft >= rght) {
      throw new BusinessException(ErrorCode.INVALID_NESTED_SET_VALUES);
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

  private void validateDiscountRates(BigDecimal discountRate, BigDecimal pgDiscountRate) {
    if (discountRate != null
        && (discountRate.compareTo(BigDecimal.ZERO) < 0
            || discountRate.compareTo(BigDecimal.valueOf(100)) > 0)) {
      throw new BusinessException(ErrorCode.INVALID_DISCOUNT_RATE);
    }
    if (pg
        && pgDiscountRate != null
        && (pgDiscountRate.compareTo(BigDecimal.ZERO) < 0
            || pgDiscountRate.compareTo(BigDecimal.valueOf(100)) > 0)) {
      throw new BusinessException(ErrorCode.INVALID_PG_DISCOUNT_RATE);
    }
  }
}
