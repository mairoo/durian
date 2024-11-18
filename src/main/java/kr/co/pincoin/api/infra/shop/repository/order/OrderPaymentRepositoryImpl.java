package kr.co.pincoin.api.infra.shop.repository.order;

import kr.co.pincoin.api.domain.shop.repository.order.OrderPaymentRepository;
import kr.co.pincoin.api.infra.shop.mapper.order.OrderPaymentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderPaymentRepositoryImpl implements OrderPaymentRepository {
    private final OrderPaymentJpaRepository orderPaymentJpaRepository;

    private final OrderPaymentQueryRepository orderPaymentQueryRepository;

    private final OrderPaymentMapper orderPaymentMapper;
}