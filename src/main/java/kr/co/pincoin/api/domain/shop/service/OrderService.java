package kr.co.pincoin.api.domain.shop.service;

import kr.co.pincoin.api.domain.shop.repository.order.OrderProductRepository;
import kr.co.pincoin.api.domain.shop.repository.order.OrderProductVoucherRepository;
import kr.co.pincoin.api.domain.shop.repository.order.OrderRepository;
import kr.co.pincoin.api.domain.shop.repository.product.ProductRepository;
import kr.co.pincoin.api.domain.shop.repository.product.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {
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

}
