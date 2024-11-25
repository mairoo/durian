package kr.co.pincoin.api.infra.shop.repository.product;

import kr.co.pincoin.api.domain.shop.model.product.Voucher;
import kr.co.pincoin.api.domain.shop.model.product.enums.VoucherStatus;
import kr.co.pincoin.api.domain.shop.repository.product.VoucherRepository;
import kr.co.pincoin.api.infra.shop.entity.product.VoucherEntity;
import kr.co.pincoin.api.infra.shop.mapper.product.VoucherMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class VoucherRepositoryImpl implements VoucherRepository {
    private final VoucherJpaRepository voucherJpaRepository;

    private final VoucherQueryRepository voucherQueryRepository;

    private final VoucherMapper voucherMapper;

    @Override
    public Voucher save(Voucher voucher) {
        return voucherMapper.toModel(voucherJpaRepository.save(voucherMapper.toEntity(voucher)));
    }

    @Override
    public Optional<Voucher> findByCode(String code) {
        return voucherJpaRepository.findByCode(code)
                .map(voucherMapper::toModel);
    }

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