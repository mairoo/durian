package kr.co.pincoin.api.infra.shop.repository.order;

import kr.co.pincoin.api.domain.shop.repository.order.OrderProductVoucherRepository;
import kr.co.pincoin.api.infra.shop.mapper.order.OrderProductVoucherMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderProductVoucherRepositoryImpl implements OrderProductVoucherRepository {
    private final OrderProductVoucherJpaRepository orderProductVoucherJpaRepository;

    private final OrderProductVoucherQueryRepository orderProductVoucherQueryRepository;

    private final OrderProductVoucherMapper orderProductVoucherMapper;
}