package kr.co.pincoin.api.domain.shop.repository.order;

import kr.co.pincoin.api.domain.shop.model.order.OrderProductVoucher;

import java.util.List;

public interface OrderProductVoucherRepository {
    List<OrderProductVoucher>
    saveAll(List<OrderProductVoucher> orderProductsVouchers);

    List<OrderProductVoucher> findAllByOrderProductOrderId(Long orderId);
}