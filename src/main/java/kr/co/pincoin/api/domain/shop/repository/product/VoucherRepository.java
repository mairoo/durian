package kr.co.pincoin.api.domain.shop.repository.product;

import kr.co.pincoin.api.domain.shop.model.product.Voucher;
import kr.co.pincoin.api.domain.shop.model.product.enums.VoucherStatus;

import java.util.List;
import java.util.Optional;

public interface VoucherRepository {
    Voucher save(Voucher voucher);

    Optional<Voucher> findByCode(String id);

    List<Voucher>
    findTopNByProductCodeAndStatusOrderByIdAsc(String productCode,
                                               VoucherStatus status,
                                               int limit);

}