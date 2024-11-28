package kr.co.pincoin.api.app.admin.order.service;

import jakarta.persistence.EntityNotFoundException;
import kr.co.pincoin.api.app.member.order.request.OrderCreateRequest;
import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.domain.auth.repository.profile.ProfileRepository;
import kr.co.pincoin.api.domain.auth.repository.user.UserRepository;
import kr.co.pincoin.api.domain.shop.model.order.Order;
import kr.co.pincoin.api.domain.shop.model.order.condition.OrderSearchCondition;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderStatus;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderVisibility;
import kr.co.pincoin.api.domain.shop.repository.order.OrderPaymentRepository;
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
                             OrderPaymentRepository orderPaymentRepository,
                             OrderProductRepository orderProductRepository,
                             OrderProductVoucherRepository orderProductVoucherRepository,
                             VoucherRepository voucherRepository,
                             ProfileRepository profileRepository,
                             UserRepository userRepository) {
        super(orderRepository,
              productRepository,
              orderPaymentRepository,
              orderProductRepository,
              orderProductVoucherRepository,
              voucherRepository,
              profileRepository);

        this.userRepository = userRepository;
    }

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
    @Transactional
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
    @Transactional
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

    /**
     * 주문 검증 상태로 변경하는 메소드
     *
     * @param order 상태를 변경할 주문
     * @throws IllegalStateException 변경 불가능한 주문 상태인 경우
     */
    @Transactional
    public void verifyOrder(Order order) {
        if (order.getStatus() == OrderStatus.PAYMENT_COMPLETED || order.getStatus() == OrderStatus.UNDER_REVIEW) {
            order.updateStatus(OrderStatus.PAYMENT_VERIFIED);
            orderRepository.save(order);
        } else {
            throw new IllegalStateException("결제 완료 또는 검토중 상태의 주문만 검증할 수 있습니다.");
        }
    }

    /**
     * 검증 완료된 주문을 검토중 상태로 변경하는 메소드
     */
    @Transactional
    public void unverifyOrder(Order order) {
        if (order.getStatus() == OrderStatus.PAYMENT_VERIFIED) {
            order.updateStatus(OrderStatus.UNDER_REVIEW);
            orderRepository.save(order);
        } else {
            throw new IllegalStateException("검증 완료 상태의 주문만 변경할 수 있습니다.");
        }
    }
}
