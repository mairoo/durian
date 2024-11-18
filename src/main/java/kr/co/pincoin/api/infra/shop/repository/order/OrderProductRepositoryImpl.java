package kr.co.pincoin.api.infra.shop.repository.order;

import kr.co.pincoin.api.domain.shop.repository.order.OrderProductRepository;
import kr.co.pincoin.api.infra.shop.mapper.order.OrderProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderProductRepositoryImpl implements OrderProductRepository {
    private final OrderProductJpaRepository orderProductJpaRepository;

    private final OrderProductQueryRepository orderProductQueryRepository;

    private final OrderProductMapper orderProductMapper;
}