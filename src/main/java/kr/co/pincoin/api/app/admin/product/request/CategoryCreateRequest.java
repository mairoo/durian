package kr.co.pincoin.api.app.admin.product.request;

import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JSON 역직렬화를 위한 기본 생성자
public class CategoryCreateRequest {
  private String title;

  private String slug;

  private BigDecimal discountRate;

  private Boolean pg;

  private BigDecimal pgDiscountRate;

  private Long parentId;

  private Long storeId;
}
