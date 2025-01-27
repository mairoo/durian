package kr.co.pincoin.api.domain.shop.event.order;

import java.util.List;
import kr.co.pincoin.api.domain.shop.model.order.Order;
import kr.co.pincoin.api.domain.shop.model.order.OrderPayment;
import kr.co.pincoin.api.domain.shop.model.order.OrderProduct;
import lombok.Getter;

@Getter
public class OrderPaymentCompletedEvent extends OrderEvent {

  private final OrderPayment payment;

  private final List<OrderProduct> orderProducts;

  public OrderPaymentCompletedEvent(
      Order order, OrderPayment payment, List<OrderProduct> orderProducts) {
    super(order);

    this.payment = payment;
    this.orderProducts = orderProducts;
  }
}
