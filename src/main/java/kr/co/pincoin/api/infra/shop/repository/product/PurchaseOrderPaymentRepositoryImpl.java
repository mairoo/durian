package kr.co.pincoin.api.infra.shop.repository.product;

import kr.co.pincoin.api.domain.shop.repository.product.PurchaseOrderPaymentRepository;
import kr.co.pincoin.api.infra.shop.mapper.product.PurchaseOrderPaymentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PurchaseOrderPaymentRepositoryImpl implements PurchaseOrderPaymentRepository {

  private final PurchaseOrderPaymentJpaRepository jpaRepository;

  private final PurchaseOrderPaymentQueryRepository queryRepository;

  private final PurchaseOrderPaymentMapper mapper;
}
