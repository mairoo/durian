package kr.co.pincoin.api.domain.shop.event.order;

import kr.co.pincoin.api.domain.shop.model.order.Order;

public class OrderVoidedEvent extends OrderEvent {

    public OrderVoidedEvent(Order order) {
        super(order);
    }
}
