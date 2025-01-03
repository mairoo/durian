package kr.co.pincoin.api.infra.shop.repository.product;

import java.util.List;
import kr.co.pincoin.api.domain.shop.model.product.enums.VoucherStatus;
import kr.co.pincoin.api.infra.shop.entity.product.VoucherEntity;
import kr.co.pincoin.api.infra.shop.repository.product.projection.ProductVoucherCount;

public interface VoucherQueryRepository {
  List<VoucherEntity> findAllByProductCodesAndVoucherStatus(
      List<String> productCodes, VoucherStatus status);

  List<ProductVoucherCount> countByProductCodesAndStatus(
      List<String> productCodes, VoucherStatus status);
}
