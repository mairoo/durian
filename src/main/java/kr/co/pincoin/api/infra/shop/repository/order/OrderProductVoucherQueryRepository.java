package kr.co.pincoin.api.infra.shop.repository.order;

import java.util.List;
import kr.co.pincoin.api.infra.shop.repository.order.projection.OrderProductVoucherProjection;

public interface OrderProductVoucherQueryRepository {
  List<OrderProductVoucherProjection> findAllByOrderProductOrderId(Long orderId);
}
