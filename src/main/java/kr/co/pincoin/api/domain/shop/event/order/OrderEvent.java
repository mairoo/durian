package kr.co.pincoin.api.domain.shop.event.order;

import java.time.LocalDateTime;
import kr.co.pincoin.api.domain.shop.model.order.Order;
import lombok.Getter;

@Getter
public abstract class OrderEvent {

    private final Order order;

    private final LocalDateTime occurredAt;

    protected OrderEvent(Order order) {
        this.order = order;
        this.occurredAt = LocalDateTime.now();
    }
}
