package kr.co.pincoin.api.domain.shop.model.product;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import kr.co.pincoin.api.domain.shop.model.product.enums.ProductStatus;
import kr.co.pincoin.api.domain.shop.model.product.enums.ProductStock;
import kr.co.pincoin.api.domain.shop.model.store.Store;
import kr.co.pincoin.api.infra.shop.entity.product.CategoryEntity;
import kr.co.pincoin.api.infra.shop.entity.product.ProductEntity;
import kr.co.pincoin.api.infra.shop.entity.store.StoreEntity;
import lombok.Builder;
import lombok.Getter;
import org.springframework.lang.Nullable;

@Getter
public class Product {
  // 핵심 식별 정보 (불변)
  private final Long id;
  private final String code;

  // 상품 기본 정보 (불변)
  private final String name;
  private final String subtitle;
  private final Boolean pg;

  // 네이버 관련 정보 (불변)
  private final Boolean naverPartner;
  private final String naverPartnerTitle;
  private final String naverPartnerTitlePg;
  private final String naverAttribute;

  // 연관 관계 (불변)
  private final Store store;

  // 생성/수정 시간 (불변)
  private final LocalDateTime created;
  private final LocalDateTime modified;

  // 상태 및 재고 정보 (가변)
  private Category category;
  private ProductStatus status;
  private ProductStock stock;
  private BigDecimal listPrice;
  private BigDecimal sellingPrice;
  private BigDecimal pgSellingPrice;
  private Integer minimumStockLevel;
  private Integer maximumStockLevel;
  private Integer stockQuantity;
  private String description;
  private Integer position;

  // 리뷰 카운트 (가변)
  private Integer reviewCount;
  private Integer reviewCountPg;

  // 삭제 상태 (가변)
  private Boolean isRemoved;

  @Builder
  private Product(
      // 불변 필드 (핵심 식별 정보)
      Long id,
      String code,

      // 불변 필드 (상품 기본 정보)
      String name,
      String subtitle,
      Boolean pg,

      // 불변 필드 (네이버 관련 정보)
      Boolean naverPartner,
      String naverPartnerTitle,
      String naverPartnerTitlePg,
      String naverAttribute,

      // 불변 필드 (연관 관계)
      @Nullable Category category,
      Store store,

      // 불변 필드 (생성/수정 시간)
      LocalDateTime created,
      LocalDateTime modified,

      // 가변 필드 (상태 및 재고 정보)
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

      // 가변 필드 (리뷰 카운트)
      Integer reviewCount,
      Integer reviewCountPg,

      // 가변 필드 (삭제 상태)
      Boolean isRemoved) {

    // 불변 필드 초기화
    this.id = id;
    this.code = code;
    this.name = name;
    this.subtitle = subtitle;
    this.pg = pg;
    this.naverPartner = naverPartner;
    this.naverPartnerTitle = naverPartnerTitle;
    this.naverPartnerTitlePg = naverPartnerTitlePg;
    this.naverAttribute = naverAttribute;
    this.category = category;
    this.store = store;
    this.created = created;
    this.modified = modified;

    // 가변 필드 초기화
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

  // 팩토리 메소드
  public static Product of(
      // 상품 기본 정보 (불변)
      String name,
      String subtitle,
      String code,
      Boolean pg,

      // 연관 관계 (불변)
      Store store,

      // 상태 및 재고 정보 (가변)
      @Nullable Category category,
      BigDecimal listPrice,
      BigDecimal sellingPrice,
      BigDecimal pgSellingPrice,
      Integer minimumStockLevel,
      Integer maximumStockLevel) {
    return Product.builder()
        // 상품 기본 정보 (불변)
        .name(name)
        .subtitle(subtitle)
        .code(code)
        .pg(pg)

        // 연관 관계 (불변)
        .store(store)

        // 상태 및 재고 정보 (가변)
        .category(category)
        .listPrice(listPrice)
        .sellingPrice(sellingPrice)
        .pgSellingPrice(pgSellingPrice)
        .minimumStockLevel(minimumStockLevel)
        .maximumStockLevel(maximumStockLevel)
        .status(ProductStatus.ENABLED)
        .stock(ProductStock.IN_STOCK)
        .stockQuantity(0)

        // 리뷰 카운트 초기값
        .reviewCount(0)
        .reviewCountPg(0)

        // 삭제 상태 초기값
        .isRemoved(false)
        .build();
  }

  // 엔티티 변환 메소드도 동일한 순서로
  public ProductEntity toEntity() {
    ProductEntity.ProductEntityBuilder builder =
        ProductEntity.builder()
            // 핵심 식별 정보 (불변)
            .id(this.id)
            .code(this.code)

            // 상품 기본 정보 (불변)
            .name(this.name)
            .subtitle(this.subtitle)
            .pg(this.pg)

            // 네이버 관련 정보 (불변)
            .naverPartner(this.naverPartner)
            .naverPartnerTitle(this.naverPartnerTitle)
            .naverPartnerTitlePg(this.naverPartnerTitlePg)
            .naverAttribute(this.naverAttribute)
            .store(StoreEntity.builder().id(1L).build())

            // 상태 및 재고 정보 (가변)
            .status(this.status)
            .stock(this.stock)
            .listPrice(this.listPrice)
            .sellingPrice(this.sellingPrice)
            .pgSellingPrice(this.pgSellingPrice)
            .minimumStockLevel(this.minimumStockLevel)
            .maximumStockLevel(this.maximumStockLevel)
            .stockQuantity(this.stockQuantity)
            .description(this.description)
            .position(this.position)

            // 리뷰 카운트 (가변)
            .reviewCount(this.reviewCount)
            .reviewCountPg(this.reviewCountPg);

    // Fetch join 사용하지 않은 경우
    //
    // 1. product.getCategory()
    //  category = product.getCategory();
    // 결과: 프록시 객체 반환 (예: CategoryProxy@123)
    // DB 쿼리: 발생하지 않음

    // 2. product.getCategory().getId()
    //  Long id = product.getCategory().getId();
    // 결과: ID 값 반환 (예: 1)
    // DB 쿼리: 발생하지 않음 (프록시도 ID는 가지고 있음)

    // 3. product.getCategory().getName()
    // String name = product.getCategory().getName();
    // 결과: 카테고리 이름 반환 (예: "Electronics")
    // DB 쿼리: 발생함 (SELECT * FROM category WHERE id = ?)
    // 이때 실제 Category 엔티티가 로딩됨

    // Fetch join 사용한 경우
    //
    // 1. product.getCategory()
    // Category category = product.getCategory();
    // 결과: 실제 Category 엔티티 반환 (예: Category@456)
    // DB 쿼리: 이미 조회 시점에 실행되었음

    // 2. product.getCategory().getId()
    // Long id = product.getCategory().getId();
    // 결과: ID 값 반환 (예: 1)
    // DB 쿼리: 발생하지 않음 (이미 로딩되어 있음)

    // 3. product.getCategory().getName()
    // String name = product.getCategory().getName();
    // 결과: 카테고리 이름 반환 (예: "Electronics")
    // DB 쿼리: 발생하지 않음 (이미 로딩되어 있음)

    if (this.category != null) {
      builder.category(
          CategoryEntity.builder()
              .id(this.category.getId()) // getId()는 프록시에서도 안전하게 조회 가능
              .build());
    }

    return builder.build();
  }

  // 상태 및 재고 정보 (가변)
  public void updateCategory(@Nullable Category category) {
    this.category = category;
  }

  public void updateStatus(ProductStatus status) {
    if (status == null) {
      throw new IllegalArgumentException("Status cannot be null");
    }
    this.status = status;
  }

  public void updatePrices(BigDecimal listPrice, BigDecimal sellingPrice) {
    validatePrices(listPrice, sellingPrice);
    this.listPrice = listPrice;
    this.sellingPrice = sellingPrice;
  }

  public void updatePgSellingPrice(BigDecimal pgSellingPrice) {
    this.pgSellingPrice = pgSellingPrice;
  }

  public void updateStockLevels(Integer minimumStockLevel, Integer maximumStockLevel) {
    this.minimumStockLevel = minimumStockLevel;
    this.maximumStockLevel = maximumStockLevel;
  }

  public void updateStockQuantity(int stockQuantity) {
    validateStockQuantity(stockQuantity);
    this.stockQuantity = stockQuantity;
    this.stock = calculateStockStatus();
  }

  public void updateDescription(String description) {
    this.description = description;
  }

  public void updatePosition(Integer position) {
    this.position = position;
  }

  // 리뷰 카운트 (가변)
  public void incrementReviewCount(boolean isPgPurchase) {
    if (isPgPurchase) {
      this.reviewCountPg++;
    }
    this.reviewCount++;
  }

  // 삭제 상태 (가변)
  public void softDelete() {
    this.isRemoved = true;
  }

  public void restore() {
    this.isRemoved = false;
  }

  // 비즈니스 계산 메소드
  public ProductStock calculateStockStatus() {
    if (stockQuantity == 0) {
      return ProductStock.SOLD_OUT;
    }

    return ProductStock.IN_STOCK;
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

  // Validation 메소드들
  private void validatePrices(BigDecimal listPrice, BigDecimal sellingPrice) {
    if (listPrice == null || sellingPrice == null) {
      throw new IllegalArgumentException("Prices cannot be null");
    }
    if (listPrice.compareTo(BigDecimal.ZERO) < 0 || sellingPrice.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("Prices cannot be negative");
    }
    if (sellingPrice.compareTo(listPrice) > 0) {
      throw new IllegalArgumentException("Selling price cannot be greater than list price");
    }
  }

  private void validateStockQuantity(int stockQuantity) {
    if (stockQuantity < 0) {
      throw new IllegalArgumentException("Stock quantity cannot be negative");
    }
  }
}
