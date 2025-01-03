package kr.co.pincoin.api.infra.shop.repository.product;

import java.util.List;
import kr.co.pincoin.api.domain.shop.model.product.enums.VoucherStatus;
import kr.co.pincoin.api.infra.shop.repository.product.projection.ProductVoucherCount;
import kr.co.pincoin.api.infra.shop.repository.product.projection.VoucherProjection;
import org.springframework.data.domain.Pageable;

public interface VoucherQueryRepository {

  List<VoucherProjection> findAllByProductCodeAndStatus(
      String productCode, VoucherStatus status, Pageable pageable);

  List<ProductVoucherCount> countByProductCodesAndStatus(
      List<String> productCodes, VoucherStatus status);
}
