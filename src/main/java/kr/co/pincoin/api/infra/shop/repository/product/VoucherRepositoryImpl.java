package kr.co.pincoin.api.infra.shop.repository.product;

import kr.co.pincoin.api.domain.shop.repository.product.VoucherRepository;
import kr.co.pincoin.api.infra.shop.mapper.product.VoucherMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class VoucherRepositoryImpl implements VoucherRepository {
    private final VoucherJpaRepository voucherJpaRepository;

    private final VoucherQueryRepository voucherQueryRepository;

    private final VoucherMapper voucherMapper;
}