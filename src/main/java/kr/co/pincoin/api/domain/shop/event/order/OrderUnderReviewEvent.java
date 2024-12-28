package kr.co.pincoin.api.domain.shop.event.order;

import kr.co.pincoin.api.domain.shop.model.order.Order;

public class OrderUnderReviewEvent extends OrderEvent {

    public OrderUnderReviewEvent(Order order) {
        super(order);
    }
}
