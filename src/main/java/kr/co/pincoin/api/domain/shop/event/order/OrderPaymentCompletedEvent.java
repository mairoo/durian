package kr.co.pincoin.api.domain.shop.event.order;

import kr.co.pincoin.api.domain.shop.model.order.Order;
import kr.co.pincoin.api.domain.shop.model.order.OrderPayment;
import lombok.Getter;

@Getter
public class OrderPaymentCompletedEvent extends OrderEvent {

    private final OrderPayment payment;

    public OrderPaymentCompletedEvent(Order order, OrderPayment payment) {
        super(order);
        this.payment = payment;
    }
}
