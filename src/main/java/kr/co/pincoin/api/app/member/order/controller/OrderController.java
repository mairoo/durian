package kr.co.pincoin.api.app.member.order.controller;

import kr.co.pincoin.api.app.member.order.response.OrderResponse;
import kr.co.pincoin.api.app.member.order.service.OrderService;
import kr.co.pincoin.api.domain.shop.model.order.Order;
import kr.co.pincoin.api.domain.shop.model.order.condition.OrderSearchCondition;
import kr.co.pincoin.api.global.response.page.PageResponse;
import kr.co.pincoin.api.global.response.success.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getMyOrders(
            @ModelAttribute OrderSearchCondition condition,
            @PageableDefault(size = 20) Pageable pageable
//            @AuthenticationPrincipal User user
                                                                               ) {
        Page<Order> orders = orderService.getMyOrders(209745, condition, pageable);
        Page<OrderResponse> responses = orders.map(OrderResponse::from);

        return ResponseEntity.ok(ApiResponse.of(PageResponse.from(responses)));
    }

    /**
     * 내 주문 상세 조회
     */
    @GetMapping("/{orderNo}")
    public ResponseEntity<Void>
    getMyOrder(
//            @CurrentUser User user,
            @PathVariable String orderNo) {

        Order order = orderService.getMyOrder(209745, orderNo);


        log.error("{}", order.getOrderNo());

        return ResponseEntity.ok(null);
    }

    private List<Sort.Order> createSortOrders(String[] sort) {
        return Arrays.stream(sort)
                .map(s -> {
                    String[] parts = s.split(",");
                    return new Sort.Order(
                            parts.length > 1 && parts[1].equalsIgnoreCase("desc")
                                    ? Sort.Direction.DESC
                                    : Sort.Direction.ASC,
                            parts[0]
                    );
                })
                .collect(Collectors.toList());
    }
}