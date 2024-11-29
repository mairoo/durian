package kr.co.pincoin.api.app.admin.product.request;

import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JSON 역직렬화를 위한 기본 생성자
public class ProductCreateRequest {
  private String name;

  private String subtitle;

  private String code;

  private BigDecimal listPrice;

  private BigDecimal sellingPrice;

  private Boolean pg;

  private BigDecimal pgSellingPrice;

  private Integer minimumStockLevel;

  private Integer maximumStockLevel;

  private Long categoryId;

  private Long storeId;
}
