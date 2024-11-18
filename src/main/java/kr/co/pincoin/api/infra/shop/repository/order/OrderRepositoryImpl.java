package kr.co.pincoin.api.infra.shop.repository.order;

import kr.co.pincoin.api.domain.shop.repository.order.OrderRepository;
import kr.co.pincoin.api.infra.shop.mapper.order.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {
    private final OrderJpaRepository orderJpaRepository;

    private final OrderQueryRepository orderQueryRepository;

    private final OrderMapper orderMapper;
}