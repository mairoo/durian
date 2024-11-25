package kr.co.pincoin.api.app.admin.order.service;

import jakarta.persistence.EntityNotFoundException;
import kr.co.pincoin.api.app.member.order.request.OrderCreateRequest;
import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.domain.auth.repository.user.UserRepository;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AdminOrderService extends AbstractOrderService {
    private final UserRepository userRepository;

    public AdminOrderService(OrderRepository orderRepository,
                             ProductRepository productRepository,
                             OrderProductRepository orderProductRepository,
                             OrderProductVoucherRepository orderProductVoucherRepository,
                             VoucherRepository voucherRepository,
                             UserRepository userRepository) {
        super(orderRepository,
              productRepository,
              orderProductRepository,
              orderProductVoucherRepository,
              voucherRepository);

        this.userRepository = userRepository;
    }

    // 사용자가 주문의 상태를 인증완료 처리로 변경한다.

    // 관리자단
    //- 주문 결제 처리 - POST
    //- 주문 결제 내역 삭제 - POST
    //- 주문 결제 완료 처리 - POST
    //- 주문 결제 완료 취소 - POST

    /**
     * 주문 목록 조회
     */
    public Page<Order>
    getOrders(OrderSearchCondition condition,
              Pageable pageable) {
        return orderRepository.searchOrders(condition, pageable);
    }

    /**
     * 주문 조회
     */
    public Order
    getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다."));
    }

    /**
     * 주문 삭제 처리 (soft delete)
     */
    public void
    deleteOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다."));

        // 이미 삭제된 주문인지 확인
        if (Boolean.TRUE.equals(order.getRemoved())) {
            throw new IllegalStateException("이미 삭제된 주문입니다.");
        }

        orderRepository.softDelete(order.getId());
    }

    /**
     * 주문 숨김 처리
     */
    public void
    hideOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다."));

        // 이미 숨김 처리된 주문인지 확인
        if (OrderVisibility.HIDDEN.equals(order.getVisibility())) {
            throw new IllegalStateException("이미 숨김 처리된 주문입니다.");
        }

        order.updateVisibility(OrderVisibility.HIDDEN);
        orderRepository.save(order);
    }

    /**
     * 관리자용 신규 주문
     */
    @Transactional
    public Order
    createOrder(Integer userId,
                OrderCreateRequest request,
                ClientUtils.ClientInfo clientInfo) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다: " + userId));

        return createOrderInternal(user, request, clientInfo);
    }

    /**
     * 관리자용 재주문
     */
    @Transactional
    public Order
    reorder(Integer userId,
            String orderNo,
            ClientUtils.ClientInfo clientInfo) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다: " + userId));

        return createReorderInternal(user.getId(), orderNo, clientInfo);
    }

    /**
     * 관리자용 수동 발권 처리
     */
    @Transactional
    public Order
    issueVouchers(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다: " + orderId));
        return issueVouchersInternal(order);
    }

    /**
     * 관리자용 환불 처리
     */
    @Transactional
    public Order
    completeRefund(Long orderId) {
        Order refundOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다."));

        return completeRefundInternal(refundOrder);
    }
}
