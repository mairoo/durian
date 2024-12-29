package kr.co.pincoin.api.domain.shop.event.order;

import java.util.List;
import kr.co.pincoin.api.domain.shop.model.order.Order;
import kr.co.pincoin.api.domain.shop.model.order.OrderProduct;
import lombok.Getter;

@Getter
public class OrderCreatedEvent extends OrderEvent {

  private final List<OrderProduct> orderProducts;

  public OrderCreatedEvent(Order order, List<OrderProduct> orderProducts) {
    super(order);

    this.orderProducts = orderProducts;
  }
}
