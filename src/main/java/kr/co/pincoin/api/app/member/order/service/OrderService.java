package kr.co.pincoin.api.app.member.order.service;

import jakarta.persistence.EntityNotFoundException;
import kr.co.pincoin.api.app.member.order.request.OrderCreateRequest;
import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.domain.shop.model.order.Order;
import kr.co.pincoin.api.domain.shop.model.order.condition.OrderSearchCondition;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderVisibility;
import kr.co.pincoin.api.domain.shop.repository.order.OrderRepository;
import kr.co.pincoin.api.global.utils.ClientUtils;
import kr.co.pincoin.api.infra.shop.service.OrderPersistenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderPersistenceService orderPersistenceService;

    private final OrderRepository orderRepository;

    /**
     * 내 주문 목록 조회
     */
    public Page<Order>
    getMyOrders(User user,
                OrderSearchCondition condition,
                Pageable pageable) {
        OrderSearchCondition searchCondition = Optional.ofNullable(condition).orElseGet(OrderSearchCondition::empty);

        Pageable pageRequest = Optional.ofNullable(pageable).orElse(Pageable.unpaged());

        return orderRepository.searchOrders(searchCondition.withUserId(user.getId()), pageRequest);
    }

    /**
     * 내 주문 조회
     */
    public Order
    getMyOrder(User user,
               String orderNo) {
        return findMyOrder(user.getId(), orderNo);
    }

    /**
     * 내 주문 삭제 처리 (soft delete)
     */
    @Transactional
    public void
    deleteMyOrder(User user,
                  String orderNo) {
        Order order = findMyOrder(user.getId(), orderNo);

        // 이미 삭제된 주문인지 확인
        if (Boolean.TRUE.equals(order.getRemoved())) {
            throw new IllegalStateException("이미 삭제된 주문입니다.");
        }

        order.softDelete();

        orderRepository.save(order);
    }

    /**
     * 내 주문 숨김 처리
     */
    @Transactional
    public void
    hideMyOrder(User user,
                String orderNo) {
        Order order = findMyOrder(user.getId(), orderNo);

        // 이미 숨김 처리된 주문인지 확인
        if (OrderVisibility.HIDDEN.equals(order.getVisibility())) {
            throw new IllegalStateException("이미 숨김 처리된 주문입니다.");
        }

        order.updateVisibility(OrderVisibility.HIDDEN);
        orderRepository.save(order);
    }

    /**
     * 고객 신규 주문
     */
    @Transactional
    public Order
    createOrder(OrderCreateRequest request,
                User user,
                ClientUtils.ClientInfo clientInfo) {
        return orderPersistenceService.createOrder(user, request, clientInfo);
    }

    /**
     * 고객 재주문
     */
    @Transactional
    public Order
    reorder(User user,
            String orderNo,
            ClientUtils.ClientInfo clientInfo) {
        return orderPersistenceService.createReorder(user.getId(), orderNo, clientInfo);
    }

    /**
     * 고객 환불 요청
     */
    @Transactional
    public Order
    requestRefund(User user,
                  String message,
                  String orderNo) {
        Order order = findMyOrder(user.getId(), orderNo);

        return orderPersistenceService.requestRefund(user, order, message);
    }

    private Order
    findMyOrder(Integer userId,
                String orderNo) {
        return orderRepository.findByOrderNoAndUserId(orderNo, userId)
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다."));
    }
}
