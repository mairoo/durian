package kr.co.pincoin.api.infra.shop.repository.order;

import kr.co.pincoin.api.domain.shop.model.order.OrderProductVoucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderProductVoucherJpaRepository extends JpaRepository<OrderProductVoucher, Long> {
}