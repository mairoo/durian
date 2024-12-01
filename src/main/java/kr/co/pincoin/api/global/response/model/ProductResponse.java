package kr.co.pincoin.api.global.response.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import kr.co.pincoin.api.domain.shop.model.product.Product;
import kr.co.pincoin.api.domain.shop.model.product.enums.ProductStatus;
import kr.co.pincoin.api.domain.shop.model.product.enums.ProductStock;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductResponse {
  private final Long id;

  private final String name;

  private final String subtitle;

  private final String code;

  private final BigDecimal listPrice;

  private final BigDecimal sellingPrice;

  private final Boolean pg;

  private final BigDecimal pgSellingPrice;

  private final String description;

  private final Integer position;

  private final ProductStatus status;

  private final Integer stockQuantity;

  private final ProductStock stock;

  private final Integer minimumStockLevel;

  private final Integer maximumStockLevel;

  private final Long categoryId;

  private final String categoryTitle;

  private final Long storeId;

  private final String storeName;

  private final LocalDateTime created;

  private final LocalDateTime modified;

  public static ProductResponse from(Product product) {
    return ProductResponse.builder()
        .id(product.getId())
        .name(product.getName())
        .subtitle(product.getSubtitle())
        .code(product.getCode())
        .listPrice(product.getListPrice())
        .sellingPrice(product.getSellingPrice())
        .pg(product.getPg())
        .pgSellingPrice(product.getPgSellingPrice())
        .description(product.getDescription())
        .position(product.getPosition())
        .status(product.getStatus())
        .stockQuantity(product.getStockQuantity())
        .stock(product.getStock())
        .minimumStockLevel(product.getMinimumStockLevel())
        .maximumStockLevel(product.getMaximumStockLevel())
        .categoryId(product.getCategory().getId())
        .categoryTitle(product.getCategory().getTitle())
        .storeId(product.getStore().getId())
        .storeName(product.getStore().getName())
        .created(product.getCreated())
        .modified(product.getModified())
        .build();
  }
}
