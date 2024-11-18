package kr.co.pincoin.api.infra.shop.repository.order;

import kr.co.pincoin.api.domain.shop.repository.order.PurchaseOrderPaymentRepository;
import kr.co.pincoin.api.infra.shop.mapper.order.PurchaseOrderPaymentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PurchaseOrderPaymentRepositoryImpl implements PurchaseOrderPaymentRepository {
    private final PurchaseOrderPaymentJpaRepository purchaseOrderPaymentJpaRepository;

    private final PurchaseOrderPaymentQueryRepository purchaseOrderPaymentQueryRepository;

    private final PurchaseOrderPaymentMapper purchaseOrderPaymentMapper;
}