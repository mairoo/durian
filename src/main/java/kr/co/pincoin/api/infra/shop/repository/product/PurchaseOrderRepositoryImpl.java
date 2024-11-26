package kr.co.pincoin.api.infra.shop.repository.product;

import kr.co.pincoin.api.domain.shop.repository.product.PurchaseOrderRepository;
import kr.co.pincoin.api.infra.shop.mapper.product.PurchaseOrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PurchaseOrderRepositoryImpl implements PurchaseOrderRepository {
    private final PurchaseOrderJpaRepository purchaseOrderJpaRepository;

    private final PurchaseOrderQueryRepository purchaseOrderQueryRepository;

    private final PurchaseOrderMapper purchaseOrderMapper;
}