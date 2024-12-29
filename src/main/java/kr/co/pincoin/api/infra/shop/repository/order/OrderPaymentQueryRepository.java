package kr.co.pincoin.api.infra.shop.repository.order;

import java.util.List;
import kr.co.pincoin.api.domain.shop.model.order.OrderPaymentDetached;

public interface OrderPaymentQueryRepository {

  List<OrderPaymentDetached> findOrderPaymentsDetachedByOrderId(Long orderId);
}
