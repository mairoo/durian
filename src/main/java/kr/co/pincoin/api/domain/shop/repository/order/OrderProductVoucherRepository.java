package kr.co.pincoin.api.domain.shop.repository.order;

import java.util.List;
import java.util.Map;
import kr.co.pincoin.api.domain.shop.model.order.OrderProduct;
import kr.co.pincoin.api.domain.shop.model.order.OrderProductVoucher;
import kr.co.pincoin.api.domain.shop.model.product.Voucher;
import kr.co.pincoin.api.infra.shop.repository.order.projection.OrderProductVoucherCount;
import kr.co.pincoin.api.infra.shop.repository.order.projection.OrderProductVoucherProjection;

public interface OrderProductVoucherRepository {
  List<OrderProductVoucher> saveAll(
      List<OrderProductVoucher> orderProductVouchers,
      Map<Long, OrderProduct> originalOrderProducts,
      Map<Long, Voucher> originalVouchers);

  List<OrderProductVoucherProjection> findAllByOrderProductOrderId(Long orderId);

  List<OrderProductVoucherCount> countIssuedVouchersByOrderProducts(
      List<OrderProduct> orderProducts);
}
