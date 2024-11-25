package kr.co.pincoin.api.app.admin.order.service;

import jakarta.persistence.EntityNotFoundException;
import kr.co.pincoin.api.domain.shop.model.order.Order;
import kr.co.pincoin.api.domain.shop.model.order.condition.OrderSearchCondition;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderVisibility;
import kr.co.pincoin.api.domain.shop.repository.order.OrderProductRepository;
import kr.co.pincoin.api.domain.shop.repository.order.OrderProductVoucherRepository;
import kr.co.pincoin.api.domain.shop.repository.order.OrderRepository;
import kr.co.pincoin.api.domain.shop.repository.product.ProductRepository;
import kr.co.pincoin.api.domain.shop.repository.product.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminOrderService {
    private final OrderRepository orderRepository;

    private final ProductRepository productRepository;

    private final VoucherRepository voucherRepository;

    private final OrderProductRepository orderProductRepository;

    private final OrderProductVoucherRepository orderProductVoucherRepository;

    // 사용자가 주문서를 작성한다.

    // 사용자가 주문서 결제완료 처리한다.

    // 사용자가 주문서를 환불 요청한다.

    // 사용자가 과거 주문 중 하나를 그대로 재주문한다.

    // 사용자가 주문을 숨김 상태로 변경한다.

    // 사용자가 주문의 상태를 인증완료 처리로 변경한다.

    // 관리자단
    //- 주문 결제 처리 - POST
    //- 주문 결제 내역 삭제 - POST
    //- 주문 결제 완료 처리 - POST
    //- 주문 결제 완료 취소 - POST
    //- 상품권 발송 처리 (재고 차감, 상품권 상태 변경) - POST
    //- 주문 환불 처리 - POST
    //
    // 주문 하기
    // 재주문 하기
    // 환불 요청하기


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
}
