package kr.co.pincoin.api.infra.shop.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import kr.co.pincoin.api.domain.shop.model.order.OrderProduct;
import kr.co.pincoin.api.domain.shop.model.order.OrderProductVoucher;
import kr.co.pincoin.api.domain.shop.repository.order.OrderProductRepository;
import kr.co.pincoin.api.domain.shop.repository.order.OrderProductVoucherRepository;
import kr.co.pincoin.api.infra.shop.entity.order.OrderProductEntity;
import kr.co.pincoin.api.infra.shop.repository.order.projection.OrderProductProjection;
import kr.co.pincoin.api.infra.shop.repository.order.projection.OrderProductVoucherCount;
import kr.co.pincoin.api.infra.shop.repository.order.projection.OrderProductVoucherProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderProductVoucherPersistenceService {
  private final OrderProductRepository orderProductRepository;

  private final OrderProductVoucherRepository orderProductVoucherRepository;

  /** 주문 ID로 주문상품 바우처 목록 조회 */
  public List<OrderProductVoucherProjection> findOrderProductVouchers(Long orderId) {
    return orderProductVoucherRepository.findAllByOrderProductOrderId(orderId);
  }

  /** 주문상품 바우처 목록 일괄 저장 (배치) */
  @Transactional
  public void saveOrderProductVouchersBatch(List<OrderProductVoucher> vouchers) {
    // OrderProduct 정보를 한 번만 조회
    Long orderId = vouchers.getFirst().getOrderProduct().getOrder().getId();
    List<OrderProductProjection> orderProducts =
        orderProductRepository.findAllWithOrderByOrderId(orderId);
    Map<Long, OrderProductEntity> orderProductMap = createOrderProductMap(orderProducts);

    int batchSize = 100;
    for (int i = 0; i < vouchers.size(); i += batchSize) {
      List<OrderProductVoucher> batch =
          vouchers.subList(i, Math.min(i + batchSize, vouchers.size()));
      orderProductVoucherRepository.saveAllWithMap(batch, orderProductMap);
    }
  }

  public List<OrderProductVoucherCount> countIssuedVouchersByOrderProducts(
      List<OrderProduct> orderProducts) {
    return orderProductVoucherRepository.countIssuedVouchersByOrderProducts(orderProducts);
  }

  private Map<Long, OrderProductEntity> createOrderProductMap(
      List<OrderProductProjection> orderProducts) {
    return orderProducts.stream()
        .collect(
            Collectors.toMap(
                projection -> projection.getOrderProduct().getId(),
                OrderProductProjection::getOrderProduct));
  }
}
