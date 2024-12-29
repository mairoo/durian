package kr.co.pincoin.api.infra.shop.repository.order;

import java.util.List;
import kr.co.pincoin.api.domain.shop.model.order.OrderProductDetached;
import kr.co.pincoin.api.domain.shop.model.order.condition.OrderProductSearchCondition;
import kr.co.pincoin.api.infra.shop.entity.order.OrderEntity;
import kr.co.pincoin.api.infra.shop.entity.order.OrderProductEntity;
import kr.co.pincoin.api.infra.shop.repository.order.projection.OrderProductProjection;

public interface OrderProductQueryRepository {

  List<OrderProductEntity> findAll(OrderProductSearchCondition condition);

  List<OrderProductDetached> findAllDetached(OrderProductSearchCondition condition);

  List<OrderProductEntity> findAllWithOrderAndUser(String orderNo, Integer userId);

  List<OrderProductEntity> findAllWithOrder(OrderEntity order);

  List<OrderProductProjection> findAllWithOrderUserProfileByOrderId(Long orderId);

  List<OrderProductProjection> findAllWithOrderByOrderId(Long orderId);
}
