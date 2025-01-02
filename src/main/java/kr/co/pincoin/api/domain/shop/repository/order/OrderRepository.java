package kr.co.pincoin.api.domain.shop.repository.order;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import kr.co.pincoin.api.domain.shop.model.order.Order;
import kr.co.pincoin.api.domain.shop.model.order.condition.OrderSearchCondition;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderRepository {

  // 기본 CRUD 작업
  Order save(Order order);

  Order saveAndFlush(Order order);

  List<Order> saveAll(Collection<Order> orders);

  // ID 기반 단일 조회
  Optional<Order> findById(Long id);

  Optional<Order> findByIdWithUser(Long id);

  Optional<Integer> findUserIdById(Long id);

  // OrderNo 기반 조회
  Optional<Integer> findUserIdByOrderNo(String orderNo);

  // 사용자 관련 조회
  List<Order> findByUserId(Integer userId);

  Optional<Order> findByIdAndUserId(Long orderId, Integer userId);

  Optional<Order> findByOrderNoAndUserId(String orderNo, Integer userId);

  Optional<Order> findByOrderDetachedNoAndUserId(String orderNo, Integer userId);

  // 상태 기반 조회
  List<Order> findByStatus(OrderStatus status);

  List<Order> findSuspiciousOrders();

  // 검색/페이징
  Page<Order> searchOrders(OrderSearchCondition condition, Pageable pageable);
}
