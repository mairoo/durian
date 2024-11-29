package kr.co.pincoin.api.infra.shop.repository.order;

import java.util.List;
import kr.co.pincoin.api.domain.shop.model.order.Order;
import kr.co.pincoin.api.infra.shop.entity.order.OrderProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderProductJpaRepository extends JpaRepository<OrderProductEntity, Long> {
  @Query(
      "SELECT op FROM OrderProductEntity op "
          + "JOIN FETCH op.order o "
          + "WHERE o.orderNo = :orderNo "
          + "AND o.user.id = :userId")
  List<OrderProductEntity> findAllByOrderNoAndUserIdFetchOrder(
      @Param("orderNo") String orderNo,
      @Param("userId") Integer userId
  );

  @Query(
      "SELECT op FROM OrderProductEntity op "
          + "JOIN FETCH op.order o "
          + "WHERE op.order = :order")
  List<OrderProductEntity> findAllByOrderFetchOrder(Order order);
}
