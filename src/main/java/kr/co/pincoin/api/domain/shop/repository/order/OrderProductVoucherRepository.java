package kr.co.pincoin.api.domain.shop.repository.order;

import java.util.List;
import java.util.Map;
import kr.co.pincoin.api.domain.shop.model.order.OrderProductVoucher;
import kr.co.pincoin.api.infra.shop.entity.order.OrderProductEntity;
import kr.co.pincoin.api.infra.shop.repository.order.projection.OrderProductVoucherProjection;

public interface OrderProductVoucherRepository {
  List<OrderProductVoucherProjection> findAllByOrderProductOrderId(Long orderId);

  List<OrderProductVoucher> saveAllWithMap(
      List<OrderProductVoucher> orderProductVouchers,
      Map<Long, OrderProductEntity> orderProductMap);
}
