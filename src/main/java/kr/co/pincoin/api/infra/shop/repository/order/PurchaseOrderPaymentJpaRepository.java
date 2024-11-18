package kr.co.pincoin.api.infra.shop.repository.order;

import kr.co.pincoin.api.domain.shop.model.order.PurchaseOrderPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseOrderPaymentJpaRepository extends JpaRepository<PurchaseOrderPayment, Long> {
}