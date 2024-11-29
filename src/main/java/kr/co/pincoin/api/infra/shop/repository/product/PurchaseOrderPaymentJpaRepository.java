package kr.co.pincoin.api.infra.shop.repository.product;

import kr.co.pincoin.api.infra.shop.entity.product.PurchaseOrderPaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseOrderPaymentJpaRepository
    extends JpaRepository<PurchaseOrderPaymentEntity, Long> {}
