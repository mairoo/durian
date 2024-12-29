package kr.co.pincoin.api.domain.shop.handler.order;

import kr.co.pincoin.api.domain.shop.event.order.OrderCreatedEvent;
import kr.co.pincoin.api.domain.shop.event.order.OrderPaymentCompletedEvent;
import kr.co.pincoin.api.domain.shop.event.order.OrderReadyForShipmentEvent;
import kr.co.pincoin.api.domain.shop.event.order.OrderRefundRequestedEvent;
import kr.co.pincoin.api.domain.shop.event.order.OrderRefundedEvent;
import kr.co.pincoin.api.domain.shop.event.order.OrderShippedEvent;
import kr.co.pincoin.api.domain.shop.event.order.OrderVoidedEvent;
import kr.co.pincoin.api.domain.shop.service.OrderVoucherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderEventHandler {

  private final OrderVoucherService orderVoucherService;
  private final ApplicationEventPublisher eventPublisher;

  /** 주문 생성 시 처리 - 비동기로 주문 완료 메일 발송 */
  @Async
  @EventListener
  public void handleOrderCreated(OrderCreatedEvent event) {
    log.info(
        "[주문생성] 주문번호: {}, 이메일: {}",
        event.getOrder().getOrderNo(),
        event.getOrder().getUser().getEmail());

    event
        .getOrderProducts()
        .forEach(
            product ->
                log.info(
                    "[주문상품] 상품명: {}, 판매가: {}, 수량: {}",
                    product.getName(),
                    product.getSellingPrice(),
                    product.getQuantity()));
  }

  /** 결제 완료 시 처리 - 주문 상태에 따른 후속 처리 - UNDER_REVIEW: SMS 발송 - PAYMENT_VERIFIED: 배송 준비 이벤트 발행 */
  @Async
  @EventListener
  @Transactional
  public void handleOrderPaymentCompleted(OrderPaymentCompletedEvent event) {
    var order = event.getOrder();
    var orderNo = order.getOrderNo();

    switch (order.getStatus()) {
      case UNDER_REVIEW -> {
        log.info("[결제완료-검토중] 주문번호: {}", orderNo);
        sendAuthenticationSms(order);
      }
      case PAYMENT_COMPLETED -> {
        log.info("[결제완료-정상] 주문번호: {}", orderNo);
      }
      case PAYMENT_VERIFIED -> {
        log.info("[결제완료-인증완료] 주문번호: {}", orderNo);
        publishReadyForShipmentEvent(event);
      }
      default -> log.warn("[결제완료-미처리상태] 주문번호: {}, 상태: {}", orderNo, order.getStatus());
    }
  }

  /** 배송 준비 시 처리 - 상품권 발행 처리 */
  @Async
  @EventListener
  @Transactional
  public void handleOrderReadyForShipment(OrderReadyForShipmentEvent event) {
    var order = event.getOrder();
    log.info("[배송준비시작] 주문번호: {}", order.getOrderNo());

    try {
      orderVoucherService.issueVouchers(order, event.getOrderProducts());
    } catch (Exception e) {
      log.error("[배송준비실패] 주문번호: {}, 오류: {}", order.getOrderNo(), e.getMessage(), e);
      throw e;
    }
  }

  /** 배송 완료 시 처리 - 비동기로 배송 완료 메일 발송 */
  @Async
  @EventListener
  public void handleOrderShipped(OrderShippedEvent event) {
    var order = event.getOrder();
    log.info("[배송완료] 주문번호: {}", order.getOrderNo());
    sendShippingCompletionEmail(order);
  }

  /** 주문 취소 시 처리 */
  @EventListener
  public void handleOrderVoided(OrderVoidedEvent event) {
    var order = event.getOrder();
    log.info("[주문취소] 주문번호: {}", order.getOrderNo());
  }

  /** 환불 요청 시 처리 */
  @EventListener
  public void handleOrderRefundRequested(OrderRefundRequestedEvent event) {
    var order = event.getOrder();
    log.info("[환불요청] 주문번호: {}", order.getOrderNo());
  }

  /** 환불 완료 시 처리 */
  @EventListener
  public void handleOrderRefunded(OrderRefundedEvent event) {
    var order = event.getOrder();
    log.info("[환불완료] 주문번호: {}", order.getOrderNo());
  }

  private void publishReadyForShipmentEvent(OrderPaymentCompletedEvent event) {
    try {
      eventPublisher.publishEvent(
          new OrderReadyForShipmentEvent(event.getOrder(), event.getOrderProducts()));
      log.debug("[이벤트발행-배송준비] 주문번호: {}", event.getOrder().getOrderNo());
    } catch (Exception e) {
      log.error(
          "[이벤트발행실패-배송준비] 주문번호: {}, 오류: {}", event.getOrder().getOrderNo(), e.getMessage(), e);
      throw e;
    }
  }

  private void sendAuthenticationSms(Object order) {
    // SMS 발송 로직 구현
    log.debug("[SMS발송-인증요청] 주문번호: {}", order);
  }

  private void sendShippingCompletionEmail(Object order) {
    // 이메일 발송 로직 구현
    log.debug("[이메일발송-배송완료] 주문번호: {}", order);
  }
}
