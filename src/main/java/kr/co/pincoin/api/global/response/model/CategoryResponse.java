package kr.co.pincoin.api.global.response.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import kr.co.pincoin.api.domain.shop.model.product.Category;
import kr.co.pincoin.api.domain.shop.model.product.CategoryDetached;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryResponse {

  @JsonProperty("id")
  private final Long id;

  @JsonProperty("title")
  private final String title;

  @JsonProperty("slug")
  private final String slug;

  @JsonProperty("thumbnail")
  private final String thumbnail;

  @JsonProperty("description")
  private final String description;

  @JsonProperty("description1")
  private final String description1;

  @JsonProperty("discountRate")
  private final BigDecimal discountRate;

  @JsonProperty("pg")
  private final Boolean pg;

  @JsonProperty("pgDiscountRate")
  private final BigDecimal pgDiscountRate;

  @JsonProperty("naverSearchTag")
  private final String naverSearchTag;

  @JsonProperty("naverBrandName")
  private final String naverBrandName;

  @JsonProperty("naverMakerName")
  private final String naverMakerName;

  // 도메인 모델 객체에서 응답 객체 초기화
  public static CategoryResponse from(Category category) {
    return CategoryResponse.builder()
        .id(category.getId())
        .title(category.getTitle())
        .slug(category.getSlug())
        .thumbnail(category.getThumbnail())
        .description(category.getDescription())
        .description1(category.getDescription1())
        .discountRate(category.getDiscountRate())
        .pg(category.getPg())
        .pgDiscountRate(category.getPgDiscountRate())
        .naverSearchTag(category.getNaverSearchTag())
        .naverBrandName(category.getNaverBrandName())
        .naverMakerName(category.getNaverMakerName())
        .build();
  }

  // CategoryDetached 객체에서 응답 객체 초기화
  public static CategoryResponse from(CategoryDetached category) {
    return CategoryResponse.builder()
        .id(category.getId())
        .title(category.getTitle())
        .slug(category.getSlug())
        .thumbnail(category.getThumbnail())
        .description(category.getDescription())
        .description1(category.getDescription1())
        .discountRate(category.getDiscountRate())
        .pg(category.getPg())
        .pgDiscountRate(category.getPgDiscountRate())
        .naverSearchTag(category.getNaverSearchTag())
        .naverBrandName(category.getNaverBrandName())
        .naverMakerName(category.getNaverMakerName())
        .build();
  }
}
