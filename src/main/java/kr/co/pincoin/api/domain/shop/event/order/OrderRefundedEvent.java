package kr.co.pincoin.api.domain.shop.event.order;

import kr.co.pincoin.api.domain.shop.model.order.Order;

public class OrderRefundedEvent extends OrderEvent {
  public OrderRefundedEvent(Order order) {
    super(order);
  }
}
