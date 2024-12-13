package kr.co.pincoin.api.infra.shop.repository.order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderStatus;
import kr.co.pincoin.api.infra.shop.entity.order.OrderEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderJpaRepository extends JpaRepository<OrderEntity, Long> {
  // 기본 CRUD + 오버라이드
  @Override
  @NonNull
  @EntityGraph(attributePaths = {"user"})
  @Query(
      "SELECT DISTINCT o FROM OrderEntity o "
          + "LEFT JOIN ProfileEntity p ON p.user = o.user "
          + "WHERE o.id = :id")
  Optional<OrderEntity> findById(@NonNull Long id);

  // ID 기반 단일 조회
  @Query("SELECT o FROM OrderEntity o " + "JOIN FETCH o.user " + "WHERE o.id = :orderId")
  Optional<OrderEntity> findByIdWithUser(Long orderId);

  @Query(
      "SELECT o FROM OrderEntity o "
          + "LEFT JOIN FETCH o.user u "
          + "LEFT JOIN ProfileEntity p ON p.user = u "
          + "WHERE o.id = :orderId AND u.id = :userId")
  Optional<OrderEntity> findByIdAndUserId(
      @Param("orderId") Long orderId, @Param("userId") Integer userId);

  // OrderNo 기반 조회
  @Query(
      "SELECT o FROM OrderEntity o "
          + "LEFT JOIN FETCH o.user u "
          + "LEFT JOIN ProfileEntity p ON p.user = u "
          + "WHERE o.orderNo = :orderNo")
  Optional<OrderEntity> findByOrderNoWithUserProfile(String orderNo);

  @Query(
      "SELECT o FROM OrderEntity o "
          + "LEFT JOIN FETCH o.user u "
          + "LEFT JOIN ProfileEntity p ON p.user = u "
          + "WHERE o.orderNo = :orderNo AND u.id = :userId")
  Optional<OrderEntity> findByOrderNoAndUserId(
      @Param("orderNo") String orderNo, @Param("userId") Integer userId);

  // 사용자 관련 조회
  @Query("SELECT o FROM OrderEntity o LEFT JOIN FETCH o.user WHERE o.user.id = :userId")
  List<OrderEntity> findByUserId(Integer userId);

  // 상태 기반 조회
  @Query("SELECT o FROM OrderEntity o LEFT JOIN FETCH o.user WHERE o.status = :status")
  List<OrderEntity> findByStatus(OrderStatus status);

  @Query(
      "SELECT o FROM OrderEntity o LEFT JOIN FETCH o.user WHERE o.suspicious = true AND o.modified IS NULL")
  List<OrderEntity> findSuspiciousOrders();

  // 상태 업데이트
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