package kr.co.pincoin.api.infra.shop.repository.order;

import kr.co.pincoin.api.domain.shop.model.order.enums.OrderStatus;
import kr.co.pincoin.api.infra.shop.entity.order.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderJpaRepository extends JpaRepository<OrderEntity, Long> {
    Optional<OrderEntity> findByOrderNo(String orderNo);

    List<OrderEntity> findByUserId(Integer user_id);

    List<OrderEntity> findByStatus(OrderStatus status);

    @Query("SELECT o FROM OrderEntity o WHERE o.suspicious = true AND o.modified IS NULL")
    List<OrderEntity> findSuspiciousOrders();

    @Modifying
    @Query("UPDATE OrderEntity o SET o.status = :status WHERE o.id = :orderId")
    void updateOrderStatus(Long orderId, OrderStatus status);

    @Modifying
    @Query("UPDATE OrderEntity o SET o.transactionId = :transactionId WHERE o.id = :orderId")
    void updateTransactionId(Long orderId, String transactionId);

    @Modifying
    @Query("UPDATE OrderEntity o SET o.suspicious = true WHERE o.id = :orderId")
    void markAsSuspicious(Long orderId);

    @Modifying
    @Query("UPDATE OrderEntity o SET o.modified = :now WHERE o.id = :orderId")
    void softDelete(Long orderId, LocalDateTime now);
}