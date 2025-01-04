package kr.co.pincoin.api.infra.shop.repository.product;

import java.util.List;
import kr.co.pincoin.api.domain.shop.model.product.enums.VoucherStatus;
import kr.co.pincoin.api.infra.shop.repository.product.projection.ProductVoucherCount;

public interface VoucherQueryRepository {
  List<ProductVoucherCount> countByProductCodesAndStatus(
      List<String> productCodes, VoucherStatus status);
}
