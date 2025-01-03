package kr.co.pincoin.api.domain.shop.repository.product;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import kr.co.pincoin.api.domain.shop.model.product.Voucher;
import kr.co.pincoin.api.domain.shop.model.product.enums.VoucherStatus;
import kr.co.pincoin.api.infra.shop.repository.product.projection.ProductVoucherCount;
import kr.co.pincoin.api.infra.shop.repository.product.projection.VoucherProjection;
import org.springframework.data.domain.Pageable;

public interface VoucherRepository {

  Voucher save(Voucher voucher);

  List<Voucher> saveAll(Collection<Voucher> vouchers);

  Optional<Voucher> findById(Long id);

  Optional<Voucher> findByCode(String code);

  List<Voucher> findAllByIdIn(Collection<Long> ids);

  List<Voucher> findAllByCodeIn(Collection<String> codes);

  List<VoucherProjection> findAllVouchersByProductCode(
      String productCode, VoucherStatus status, Pageable pageable);

  List<ProductVoucherCount> countByProductCodesAndStatus(
      List<String> productCodes, VoucherStatus status);

  void delete(Voucher voucher);

  void deleteById(Long id);

  void softDelete(Voucher voucher);

  void softDeleteById(Long id);

  void restore(Voucher voucher);

  void restoreById(Long id);

  void updateStatusToSold(List<Long> voucherIds, VoucherStatus status);
}
