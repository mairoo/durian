package kr.co.pincoin.api.domain.shop.event.order;

import kr.co.pincoin.api.domain.shop.model.order.Order;

public class OrderPaymentVerifiedEvent extends OrderEvent {

    public OrderPaymentVerifiedEvent(Order order) {
        super(order);
    }
}
