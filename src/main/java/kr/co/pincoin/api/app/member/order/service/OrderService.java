package kr.co.pincoin.api.app.member.order.service;

import jakarta.persistence.EntityNotFoundException;
import kr.co.pincoin.api.app.member.order.request.OrderCreateRequest;
import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.domain.shop.model.order.Order;
import kr.co.pincoin.api.domain.shop.model.order.condition.OrderSearchCondition;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderVisibility;
import kr.co.pincoin.api.domain.shop.repository.order.OrderProductRepository;
import kr.co.pincoin.api.domain.shop.repository.order.OrderProductVoucherRepository;
import kr.co.pincoin.api.domain.shop.repository.order.OrderRepository;
import kr.co.pincoin.api.domain.shop.repository.product.ProductRepository;
import kr.co.pincoin.api.domain.shop.repository.product.VoucherRepository;
import kr.co.pincoin.api.domain.shop.service.AbstractOrderService;
import kr.co.pincoin.api.global.utils.ClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@Slf4j
public class OrderService extends AbstractOrderService {
    public OrderService(OrderRepository orderRepository,
                        ProductRepository productRepository,
                        OrderProductRepository orderProductRepository,
                        OrderProductVoucherRepository orderProductVoucherRepository,
                        VoucherRepository voucherRepository) {
        super(orderRepository,
              productRepository,
              orderProductRepository,
              orderProductVoucherRepository,
              voucherRepository);
    }

    /**
     * 내 주문 목록 조회
     */
    public Page<Order>
    getMyOrders(Integer userId,
                                   OrderSearchCondition condition,
                                   Pageable pageable) {
        return orderRepository.searchOrders(condition.withUserId(userId), pageable);
    }

    /**
     * 내 주문 조회
     */
    public Order
    getMyOrder(Integer userId,
                            String orderNo) {
        return orderRepository.findByOrderNoAndUserId(orderNo, userId)
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다."));
    }

    /**
     * 내 주문 삭제 처리 (soft delete)
     */
    @Transactional
    public void
    deleteMyOrder(Integer userId,
                              String orderNo) {
        Order order = orderRepository.findByOrderNoAndUserId(orderNo, userId)
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다."));

        // 이미 삭제된 주문인지 확인
        if (Boolean.TRUE.equals(order.getRemoved())) {
            throw new IllegalStateException("이미 삭제된 주문입니다.");
        }

        orderRepository.softDelete(order.getId());
    }

    /**
     * 내 주문 숨김 처리
     */
    @Transactional
    public void
    hideMyOrder(Integer userId,
                            String orderNo) {
        Order order = orderRepository.findByOrderNoAndUserId(orderNo, userId)
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다."));

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
    createOrder(User user,
                OrderCreateRequest request,
                ClientUtils.ClientInfo clientInfo) {
        return createOrderInternal(user, request, clientInfo);
    }

    /**
     * 고객 재주문
     */
    @Transactional
    public Order
    reorder(User user,
            String orderNo,
            ClientUtils.ClientInfo clientInfo) {
        return createReorderInternal(user.getId(), orderNo, clientInfo);
    }


    /**
     * 고객 환불 요청
     */
    @Transactional
    public Order
    requestRefund(User user,
                  String message,
                  String orderNo) {
        Order order = orderRepository.findByOrderNoAndUserId(orderNo, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다."));

        return refundRequest(user, order, message);
    }
}
