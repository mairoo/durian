package kr.co.pincoin.api.domain.shop.repository.product;

import kr.co.pincoin.api.domain.shop.model.product.Voucher;
import kr.co.pincoin.api.domain.shop.model.product.enums.VoucherStatus;

import java.util.List;

public interface VoucherRepository {
    List<Voucher>
    findTopNByProductCodeAndStatusOrderByIdAsc(String productCode,
                                               VoucherStatus status,
                                               int limit);

}