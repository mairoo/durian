package kr.co.pincoin.api.domain.shop.repository.order;

import kr.co.pincoin.api.domain.shop.model.order.OrderProduct;

import java.util.List;

public interface OrderProductRepository {
    List<OrderProduct>
    saveAll(List<OrderProduct> orderProducts);

    List<OrderProduct>
    findAllByOrderNoAndUserIdFetchOrderAndUser(String orderNo,
                                               Integer userId);
}