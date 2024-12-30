package kr.co.pincoin.api.domain.shop.repository.order;

import java.util.List;
import kr.co.pincoin.api.domain.shop.model.order.OrderProductVoucher;
import kr.co.pincoin.api.infra.shop.repository.order.projection.OrderProductVoucherProjection;

public interface OrderProductVoucherRepository {

  List<OrderProductVoucher> saveAll(List<OrderProductVoucher> orderProductsVouchers);

  List<OrderProductVoucherProjection> findAllByOrderProductOrderId(Long orderId);
}
