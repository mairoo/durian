package kr.co.pincoin.api.infra.shop.service;

import java.util.List;
import kr.co.pincoin.api.domain.shop.model.order.Order;
import kr.co.pincoin.api.domain.shop.model.order.OrderProduct;
import kr.co.pincoin.api.domain.shop.model.order.OrderProductDetached;
import kr.co.pincoin.api.domain.shop.model.order.condition.OrderProductSearchCondition;
import kr.co.pincoin.api.domain.shop.repository.order.OrderProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderProductPersistenceService {

    private final OrderProductRepository orderProductRepository;

    /**
     * 주문 ID로 주문상품 목록 조회
     */
    public List<OrderProduct> findOrderProductsByOrderId(Long orderId) {
        return orderProductRepository.findAll(OrderProductSearchCondition.ofOrderId(orderId));
    }

    /**
     * 사용자 ID와 주문번호로 주문상품 목록 조회
     */
    public List<OrderProduct> findOrderProductsByUserIdAndOrderNo(Integer userId, String orderNo) {
        return orderProductRepository.findAll(
            OrderProductSearchCondition.ofOrderNo(orderNo).withUserId(userId));
    }

    /**
     * 사용자 ID와 주문번호로 분리된 주문상품 목록 조회
     */
    public List<OrderProductDetached> findOrderProductsDetachedByUserIdAndOrderNo(
        Integer userId, String orderNo) {
        return orderProductRepository.findAllDetached(
            OrderProductSearchCondition.ofOrderNo(orderNo).withUserId(userId));
    }

    /**
     * 주문에 속한 주문상품 목록 조회
     */
    public List<OrderProduct> findOrderProductsWithOrder(Order order) {
        return orderProductRepository.findAllWithOrder(order);
    }

    /**
     * 원본 주문상품 목록 조회 (사용자 정보 포함)
     */
    public List<OrderProduct> findOriginalOrderProducts(String orderNo, Integer userId) {
        return orderProductRepository.findAllWithOrderAndUser(orderNo, userId);
    }

    /**
     * 주문상품 목록 저장
     */
    @Transactional
    public void saveOrderProducts(List<OrderProduct> orderProducts) {
        int batchSize = 100;
        for (int i = 0; i < orderProducts.size(); i += batchSize) {
            List<OrderProduct> batch =
                orderProducts.subList(i, Math.min(i + batchSize, orderProducts.size()));
            orderProductRepository.saveAll(batch);
        }
    }

    /**
     * 주문상품 목록 일괄 저장 (배치)
     */
    @Transactional
    public void saveOrderProductsBatch(List<OrderProduct> orderProducts) {
        int batchSize = 100;
        for (int i = 0; i < orderProducts.size(); i += batchSize) {
            List<OrderProduct> batch =
                orderProducts.subList(i, Math.min(i + batchSize, orderProducts.size()));
            orderProductRepository.saveAll(batch);
        }
    }
}
