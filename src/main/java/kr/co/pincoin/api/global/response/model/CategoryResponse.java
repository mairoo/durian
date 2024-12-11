package kr.co.pincoin.api.global.response.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import kr.co.pincoin.api.domain.shop.model.product.Category;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryResponse {
  private final Long id;

  private final String title;

  private final String slug;

  private final String thumbnail;

  private final String description;

  private final String description1;

  private final BigDecimal discountRate;

  private final Boolean pg;

  private final BigDecimal pgDiscountRate;

  private final String naverSearchTag;

  private final String naverBrandName;

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
}
