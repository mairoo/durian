package kr.co.pincoin.api.infra.shop.repository.product;

import kr.co.pincoin.api.domain.shop.model.product.Voucher;
import kr.co.pincoin.api.domain.shop.model.product.enums.VoucherStatus;
import kr.co.pincoin.api.domain.shop.repository.product.VoucherRepository;
import kr.co.pincoin.api.infra.shop.entity.product.VoucherEntity;
import kr.co.pincoin.api.infra.shop.mapper.product.VoucherMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class VoucherRepositoryImpl implements VoucherRepository {
    private final VoucherJpaRepository voucherJpaRepository;

    private final VoucherQueryRepository voucherQueryRepository;

    private final VoucherMapper voucherMapper;

    @Override
    public List<Voucher>
    findTopNByProductCodeAndStatusOrderByIdAsc(String productCode,
                                               VoucherStatus status,
                                               int limit) {
        List<VoucherEntity> savedEntities = voucherJpaRepository
                .findTopNByProductCodeAndStatusOrderByIdAsc(productCode,
                                                            status,
                                                            limit);

        return voucherMapper.toModelList(savedEntities);
    }
}