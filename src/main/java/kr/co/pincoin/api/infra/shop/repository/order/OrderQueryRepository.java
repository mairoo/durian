package kr.co.pincoin.api.infra.shop.repository.order;

import java.util.Optional;
import kr.co.pincoin.api.domain.shop.model.order.condition.OrderSearchCondition;
import kr.co.pincoin.api.infra.shop.entity.order.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderQueryRepository {
  // ID/OrderNo 기반 사용자 조회
  Optional<Integer> findUserIdByOrderId(Long id);

  Optional<Integer> findUserIdByOrderNo(String orderNo);

  Optional<OrderEntity> findByOrderNoAndUserId(String orderNo, Integer userId);

  // 검색/페이징
  Page<OrderEntity> searchOrders(OrderSearchCondition condition, Pageable pageable);
}
