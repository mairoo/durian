package kr.co.pincoin.api.domain.shop.repository.product;

import kr.co.pincoin.api.domain.shop.model.product.Voucher;
import kr.co.pincoin.api.domain.shop.model.product.enums.VoucherStatus;

import java.util.List;
import java.util.Optional;

public interface VoucherRepository {
    Voucher save(Voucher voucher);

    Optional<Voucher> findById(Long id);

    Optional<Voucher> findByCode(String id);

    List<Voucher>
    findTopNByProductCodeAndStatusOrderByIdAsc(String productCode,
                                               VoucherStatus status,
                                               int limit);

    void delete(Voucher voucher);

    void deleteById(Long id);

    void softDelete(Voucher voucher);

    void softDeleteById(Long id);

    void restore(Voucher voucher);

    void restoreById(Long id);
}