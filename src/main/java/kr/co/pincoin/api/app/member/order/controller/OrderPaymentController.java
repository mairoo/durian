package kr.co.pincoin.api.app.member.order.controller;

import java.util.List;
import kr.co.pincoin.api.app.member.order.service.OrderService;
import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.domain.shop.model.order.OrderPayment;
import kr.co.pincoin.api.global.response.success.ApiResponse;
import kr.co.pincoin.api.global.security.annotation.CurrentUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderPaymentController {

    private final OrderService orderService;

    @GetMapping("/{orderNo}/payments")
    public ResponseEntity<ApiResponse<List<OrderPayment>>> getMyOrderPayments(
        @CurrentUser User user,
        @PathVariable String orderNo) {
        List<OrderPayment> orderPayments = orderService.getMyOrderPayments(user,
            orderNo);

        return ResponseEntity.ok(ApiResponse.of(orderPayments));
    }
}
