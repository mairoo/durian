package kr.co.pincoin.api.domain.shop.repository.order;

import java.util.List;
import kr.co.pincoin.api.domain.shop.model.order.OrderProduct;
import kr.co.pincoin.api.domain.shop.model.order.OrderProductVoucher;
import kr.co.pincoin.api.infra.shop.repository.order.projection.OrderProductVoucherCount;
import kr.co.pincoin.api.infra.shop.repository.order.projection.OrderProductVoucherProjection;

public interface OrderProductVoucherRepository {
  void batchSave(List<OrderProductVoucher> orderProductVouchers);

  List<OrderProductVoucherProjection> findVoucherDetails(Long orderId);

  List<OrderProductVoucherCount> countActiveVouchers(List<OrderProduct> orderProducts);
}
