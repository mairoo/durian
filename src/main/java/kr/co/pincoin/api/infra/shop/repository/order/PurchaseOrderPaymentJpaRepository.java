package kr.co.pincoin.api.infra.shop.repository.order;

import kr.co.pincoin.api.infra.shop.entity.order.PurchaseOrderPaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseOrderPaymentJpaRepository extends JpaRepository<PurchaseOrderPaymentEntity, Long> {
}