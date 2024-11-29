package kr.co.pincoin.api.infra.shop.repository.product;

import kr.co.pincoin.api.infra.shop.entity.product.PurchaseOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseOrderJpaRepository extends JpaRepository<PurchaseOrderEntity, Long> {}
