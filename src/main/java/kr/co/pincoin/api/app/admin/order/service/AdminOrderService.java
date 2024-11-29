package kr.co.pincoin.api.app.admin.order.service;

import kr.co.pincoin.api.app.member.order.request.OrderCreateRequest;
import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.domain.shop.model.order.Order;
import kr.co.pincoin.api.domain.shop.model.order.condition.OrderSearchCondition;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderStatus;
import kr.co.pincoin.api.global.utils.ClientUtils;
import kr.co.pincoin.api.infra.shop.service.OrderPersistenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class AdminOrderService {
    private final OrderPersistenceService orderPersistenceService;

    /**
     * 주문 목록 조회
     */
    public Page<Order>
    getOrders(OrderSearchCondition condition,
              Pageable pageable) {
        return orderPersistenceService.searchOrders(condition, pageable);
    }

    /**
     * 주문 조회
     */
    public Order
    getOrder(Long orderId) {
        return orderPersistenceService.findOrder(orderId);
    }

    /**
     * 주문 삭제 처리 (soft delete)
     */
    @Transactional
    public void
    deleteOrder(Long orderId) {
        orderPersistenceService.softDeleteOrder(orderId);
    }

    /**
     * 주문 숨김 처리
     */
    @Transactional
    public void
    hideOrder(Long orderId) {
        orderPersistenceService.hideOrder(orderId);
    }

    /**
     * 관리자용 신규 주문
     */
    @Transactional
    public Order
    createOrder(Integer userId,
                OrderCreateRequest request,
                ClientUtils.ClientInfo clientInfo) {
        User user = orderPersistenceService.findUser(userId);
        return orderPersistenceService.createOrder(user, request, clientInfo);
    }

    /**
     * 관리자용 재주문
     */
    @Transactional
    public Order
    reorder(Integer userId,
            String orderNo,
            ClientUtils.ClientInfo clientInfo) {
        return orderPersistenceService.createReorder(userId, orderNo, clientInfo);
    }

    /**
     * 관리자용 수동 발권 처리
     */
    @Transactional
    public Order
    issueVouchers(Long orderId) {
        Order order = orderPersistenceService.findOrder(orderId);
        return orderPersistenceService.issueVouchers(order);
    }

    /**
     * 관리자용 환불 처리
     */
    @Transactional
    public Order
    completeRefund(Long orderId) {
        return orderPersistenceService.completeRefund(orderPersistenceService.findOrder(orderId));
    }

    /**
     * 주문 검증 상태로 변경하는 메소드
     *
     * @param orderId 상태를 변경할 주문
     * @throws IllegalStateException 변경 불가능한 주문 상태인 경우
     */
    @Transactional
    public void verifyOrder(Long orderId) {
        Order order = orderPersistenceService.findOrder(orderId);

        if (order.getStatus() == OrderStatus.PAYMENT_COMPLETED ||
                order.getStatus() == OrderStatus.UNDER_REVIEW) {
            order.updateStatus(OrderStatus.PAYMENT_VERIFIED);
            orderPersistenceService.save(order);  // save 메서드도 추가 필요
        } else {
            throw new IllegalStateException("결제 완료 또는 검토중 상태의 주문만 검증할 수 있습니다.");
        }
    }

    /**
     * 검증 완료된 주문을 검토중 상태로 변경하는 메소드
     */
    @Transactional
    public void unverifyOrder(Long orderId) {
        Order order = orderPersistenceService.findOrder(orderId);

        if (order.getStatus() == OrderStatus.PAYMENT_VERIFIED) {
            order.updateStatus(OrderStatus.UNDER_REVIEW);
            orderPersistenceService.save(order);  // save 메서드도 추가 필요
        } else {
            throw new IllegalStateException("검증 완료 상태의 주문만 변경할 수 있습니다.");
        }
    }
}
