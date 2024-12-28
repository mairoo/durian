package kr.co.pincoin.api.domain.shop.event.order;

import kr.co.pincoin.api.domain.shop.model.order.Order;

public class OrderRefundRequestedEvent extends OrderEvent {

    public OrderRefundRequestedEvent(Order order) {
        super(order);
    }
}
