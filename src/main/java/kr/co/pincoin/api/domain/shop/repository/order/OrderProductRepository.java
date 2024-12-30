package kr.co.pincoin.api.domain.shop.repository.order;

import java.util.List;
import kr.co.pincoin.api.domain.shop.model.order.OrderProduct;
import kr.co.pincoin.api.domain.shop.model.order.OrderProductDetached;
import kr.co.pincoin.api.domain.shop.model.order.condition.OrderProductSearchCondition;
import kr.co.pincoin.api.infra.shop.repository.order.projection.OrderProductProjection;

public interface OrderProductRepository {

  List<OrderProduct> saveAll(List<OrderProduct> orderProducts);

  List<OrderProduct> findAll(OrderProductSearchCondition condition);

  List<OrderProductDetached> findAllDetached(OrderProductSearchCondition condition);

  List<OrderProduct> findAllWithOrderAndUser(String orderNo, Integer userId);

  List<OrderProductProjection> findAllWithOrderUserProfileByOrderId(Long orderId);

  List<OrderProductProjection> findAllWithOrderByOrderId(Long orderId);
}
