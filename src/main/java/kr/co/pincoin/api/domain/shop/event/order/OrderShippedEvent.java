package kr.co.pincoin.api.domain.shop.event.order;

import kr.co.pincoin.api.domain.shop.model.order.Order;

public class OrderShippedEvent extends OrderEvent {
  public OrderShippedEvent(Order order) {
    super(order);
  }
}
