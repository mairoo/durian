package kr.co.pincoin.api.domain.shop.handler.order;

import kr.co.pincoin.api.domain.shop.event.order.OrderCreatedEvent;
import kr.co.pincoin.api.domain.shop.event.order.OrderPaymentCompletedEvent;
import kr.co.pincoin.api.domain.shop.event.order.OrderPaymentVerifiedEvent;
import kr.co.pincoin.api.domain.shop.event.order.OrderRefundRequestedEvent;
import kr.co.pincoin.api.domain.shop.event.order.OrderRefundedEvent;
import kr.co.pincoin.api.domain.shop.event.order.OrderShippedEvent;
import kr.co.pincoin.api.domain.shop.event.order.OrderUnderReviewEvent;
import kr.co.pincoin.api.domain.shop.event.order.OrderVoidedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderEventHandler {

    @Async
    @EventListener
    public void handleOrderCreated(OrderCreatedEvent event) {
        // 주문 완료 메일 발송 처리
        log.info("Order created: {} {}", event.getOrder().getOrderNo(), event.getOrder().getUser().getEmail());
    }

    @EventListener
    public void handleOrderPaymentCompletedEvent(OrderPaymentCompletedEvent event) {
        log.info("Order payment completed: {}", event.getOrder());
    }

    @Async
    @EventListener
    public void handleOrderUnderReviewEvent(OrderUnderReviewEvent event) {
        // 추가 인증 요청 sms 발송
        log.info("Order under review: {}", event.getOrder());
    }

    @Async
    @EventListener
    public void handleOrderPaymentVerifiedEvent(OrderPaymentVerifiedEvent event) {
        // 관리자에게 알림 발송

        // 고객에게 대기 요청
        log.info("Order payment verified: {}", event.getOrder());
    }

    @Async
    @EventListener
    public void handleOrderShippedEvent(OrderShippedEvent event) {
        // 상품권 발송 완료 안내 메일 발송 처리
        log.info("Order shipped: {}", event.getOrder());
    }

    @EventListener
    public void handleOrderVoidedEvent(OrderVoidedEvent event) {
        log.info("Order voided: {}", event.getOrder());
    }

    @EventListener
    public void handleOrderRefundRequestedEvent(OrderRefundRequestedEvent event) {
        log.info("Order refund requested: {}", event.getOrder());
    }

    @EventListener
    public void handleOrderRefundedEvent(OrderRefundedEvent event) {
        log.info("Order refunded: {}", event.getOrder());
    }
}
