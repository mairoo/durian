package kr.co.pincoin.api.app.member.order.service;

import jakarta.persistence.EntityNotFoundException;
import kr.co.pincoin.api.domain.shop.model.order.Order;
import kr.co.pincoin.api.domain.shop.model.order.condition.OrderSearchCondition;
import kr.co.pincoin.api.domain.shop.repository.order.OrderRepository;
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
public class OrderService {
    private final OrderRepository orderRepository;

    // 고객단
    //- 주문 영수증 PDF 생성
    //- 재주문 처리 - POST
    //- 주문 삭제 - POST (속성 변경)
    //- 주문 숨김 처리 - POST (속성 변경)
    //- 환불 요청 처리 - POST

    /**
     * 내 주문 목록 조회 (페이징)
     */
    public Page<Order> getMyOrders(Integer userId, OrderSearchCondition condition, Pageable pageable) {
        return orderRepository.searchOrders(condition.withUserId(userId), pageable);
    }

    /**
     * 내 주문 조회
     */
    public Order getMyOrder(Integer userId, String orderNo) {
        return orderRepository.findByOrderNoAndUserId(orderNo, userId)
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다."));
    }
}
