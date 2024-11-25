package kr.co.pincoin.api.app.member.order.controller;

import kr.co.pincoin.api.app.admin.order.request.OrderSearchRequest;
import kr.co.pincoin.api.app.member.order.service.OrderService;
import kr.co.pincoin.api.domain.shop.model.order.Order;
import kr.co.pincoin.api.domain.shop.model.order.condition.OrderSearchCondition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    private final OrderService orderService;

    /**
     * 내 주문 목록 조회
     */
    @GetMapping
    public ResponseEntity<Void> getMyOrders(
//            @CurrentUser User user,
            @ModelAttribute OrderSearchRequest searchRequest,
            @PageableDefault(size = 20, sort = "created", direction = Sort.Direction.DESC) Pageable pageable) {

        OrderSearchCondition searchCondition = OrderSearchCondition.builder()
                .orderNo(searchRequest.getOrderNo())
                .status(searchRequest.getStatus())
                .startDate(searchRequest.getStartDate())
                .endDate(searchRequest.getEndDate())
                .build();

        Page<Order> orders = orderService.getMyOrders(209745, searchCondition, pageable);

        log.debug("count {}", orders.getTotalElements());

        return ResponseEntity.ok(null);
    }

    /**
     * 내 주문 상세 조회
     */
    @GetMapping("/{orderNo}")
    public ResponseEntity<Void> getMyOrder(
//            @CurrentUser User user,
            @PathVariable String orderNo) {

        Order order = orderService.getMyOrder(209745, orderNo);


        log.error("{}", order.getOrderNo());

        return ResponseEntity.ok(null);
    }
}