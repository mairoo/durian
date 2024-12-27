package kr.co.pincoin.api.app.admin.product.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import kr.co.pincoin.api.app.member.product.response.ProductResponse;
import kr.co.pincoin.api.domain.shop.model.product.Product;
import kr.co.pincoin.api.domain.shop.model.product.ProductDetached;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminProductResponse extends ProductResponse {

  @JsonProperty("categoryId")
  private final Long categoryId;

  @JsonProperty("naverPartner")
  private final Boolean naverPartner;

  @JsonProperty("naverPartnerTitle")
  private final String naverPartnerTitle;

  @JsonProperty("naverPartnerTitlePg")
  private final String naverPartnerTitlePg;

  @JsonProperty("naverAttribute")
  private final String naverAttribute;

  @JsonProperty("created")
  private final LocalDateTime created;

  @JsonProperty("modified")
  private final LocalDateTime modified;

  @JsonProperty("minimumStockLevel")
  private final Integer minimumStockLevel;

  @JsonProperty("maximumStockLevel")
  private final Integer maximumStockLevel;

  @JsonProperty("stockQuantity")
  private final Integer stockQuantity;

  @JsonProperty("reviewCount")
  private final Integer reviewCount;

  @JsonProperty("reviewCountPg")
  private final Integer reviewCountPg;

  @JsonProperty("isRemoved")
  private Boolean isRemoved;

  // 생성자 외부 접근 불허 / 자식 허용
  protected AdminProductResponse(Product product) {
    super(product);

    this.categoryId = product.getCategory().getId();
    this.naverPartner = product.getNaverPartner();
    this.naverPartnerTitle = product.getNaverPartnerTitle();
    this.naverPartnerTitlePg = product.getNaverPartnerTitlePg();
    this.naverAttribute = product.getNaverAttribute();
    this.created = product.getCreated();
    this.modified = product.getModified();
    this.minimumStockLevel = product.getMinimumStockLevel();
    this.maximumStockLevel = product.getMaximumStockLevel();
    this.stockQuantity = product.getStockQuantity();
    this.reviewCount = product.getReviewCount();
    this.reviewCountPg = product.getReviewCountPg();
    this.isRemoved = product.getIsRemoved();
  }

  protected AdminProductResponse(ProductDetached product) {
    super(product);

    this.categoryId = product.getCategoryId();
    this.naverPartner = product.getNaverPartner();
    this.naverPartnerTitle = product.getNaverPartnerTitle();
    this.naverPartnerTitlePg = product.getNaverPartnerTitlePg();
    this.naverAttribute = product.getNaverAttribute();
    this.created = product.getCreated();
    this.modified = product.getModified();
    this.minimumStockLevel = product.getMinimumStockLevel();
    this.maximumStockLevel = product.getMaximumStockLevel();
    this.stockQuantity = product.getStockQuantity();
    this.reviewCount = product.getReviewCount();
    this.reviewCountPg = product.getReviewCountPg();
    this.isRemoved = product.getIsRemoved();
  }

  // 도메인 모델 객체에서 응답 객체 초기화
  public static AdminProductResponse from(Product product) {
    return new AdminProductResponse(product);
  }

  public static AdminProductResponse from(ProductDetached product) {
    return new AdminProductResponse(product);
  }
}