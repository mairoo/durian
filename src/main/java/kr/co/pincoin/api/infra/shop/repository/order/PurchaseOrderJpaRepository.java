package kr.co.pincoin.api.infra.shop.repository.order;

import kr.co.pincoin.api.infra.shop.entity.order.PurchaseOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseOrderJpaRepository extends JpaRepository<PurchaseOrderEntity, Long> {
}