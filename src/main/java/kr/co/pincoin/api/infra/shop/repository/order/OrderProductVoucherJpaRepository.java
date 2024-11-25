package kr.co.pincoin.api.infra.shop.repository.order;

import kr.co.pincoin.api.infra.shop.entity.order.OrderProductVoucherEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderProductVoucherJpaRepository extends JpaRepository<OrderProductVoucherEntity, Long> {
    @Query("SELECT opv FROM OrderProductVoucherEntity opv " +
            "JOIN FETCH opv.orderProduct op " +
            "WHERE op.order.id = :orderId")
    List<OrderProductVoucherEntity> findAllByOrderProductOrderId(@Param("orderId") Long orderId);
}