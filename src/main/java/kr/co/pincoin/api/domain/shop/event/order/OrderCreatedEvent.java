package kr.co.pincoin.api.domain.shop.event.order;

import kr.co.pincoin.api.domain.shop.model.order.Order;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderStatus;
import lombok.Getter;

@Getter
public class OrderCreatedEvent extends OrderEvent {

    private final OrderStatus status = OrderStatus.PAYMENT_PENDING;

    public OrderCreatedEvent(Order order) {
        super(order);
    }
}