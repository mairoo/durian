package kr.co.pincoin.api.infra.shop.repository.order;

import java.math.BigDecimal;
import java.util.List;
import kr.co.pincoin.api.infra.shop.entity.order.OrderEntity;
import kr.co.pincoin.api.infra.shop.entity.order.OrderPaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderPaymentJpaRepository extends JpaRepository<OrderPaymentEntity, Long> {

  @Query(
      "SELECT op "
          + "FROM OrderPaymentEntity op "
          + "WHERE op.order = :orderEntity AND op.isRemoved = false")
  List<OrderPaymentEntity> findByOrderAndRemovedFalse(
      @Param("orderEntity") OrderEntity orderEntity);

  List<OrderPaymentEntity> findByOrderId(Long orderId);

  @Query(
      "SELECT COALESCE(SUM(p.amount), 0) "
          + "FROM OrderPaymentEntity p "
          + "WHERE p.order = :orderEntity AND p.isRemoved = false")
  BigDecimal getTotalAmountByOrder(@Param("orderEntity") OrderEntity orderEntity);
}
