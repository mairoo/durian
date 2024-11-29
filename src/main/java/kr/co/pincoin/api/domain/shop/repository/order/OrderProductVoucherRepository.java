package kr.co.pincoin.api.domain.shop.repository.order;

import java.util.List;
import kr.co.pincoin.api.domain.shop.model.order.OrderProductVoucher;

public interface OrderProductVoucherRepository {
  List<OrderProductVoucher> saveAll(List<OrderProductVoucher> orderProductsVouchers);

  List<OrderProductVoucher> findAllByOrderProductOrderId(Long orderId);
}
