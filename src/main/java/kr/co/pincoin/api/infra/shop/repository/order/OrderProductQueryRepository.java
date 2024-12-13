package kr.co.pincoin.api.infra.shop.repository.order;

import java.util.List;
import kr.co.pincoin.api.domain.shop.model.order.condition.OrderProductSearchCondition;
import kr.co.pincoin.api.infra.shop.entity.order.OrderProductEntity;

public interface OrderProductQueryRepository {

    List<OrderProductEntity> findOrderProducts(OrderProductSearchCondition condition);
}
