package kr.co.pincoin.api.infra.shop.service;

import java.util.List;
import kr.co.pincoin.api.domain.shop.model.order.OrderProduct;
import kr.co.pincoin.api.domain.shop.model.order.OrderProductVoucher;
import kr.co.pincoin.api.domain.shop.repository.order.OrderProductVoucherRepository;
import kr.co.pincoin.api.infra.shop.repository.order.projection.OrderProductVoucherCount;
import kr.co.pincoin.api.infra.shop.repository.order.projection.OrderProductVoucherProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderProductVoucherPersistenceService {
  private final OrderProductVoucherRepository orderProductVoucherRepository;

  /**
   * 주문 상품 바우처 목록을 일괄 저장합니다. 트랜잭션 내에서 실행되어 원자성을 보장합니다.
   *
   * @param orderProductVouchers 저장할 주문 상품 바우처 목록
   */
  @Transactional
  public void batchSave(List<OrderProductVoucher> orderProductVouchers) {
    orderProductVoucherRepository.batchSave(orderProductVouchers);
  }

  /**
   * 주문 ID로 주문 상품 바우처 목록을 조회합니다.
   *
   * @param orderId 조회할 주문 ID
   * @return 조회된 주문 상품 바우처 프로젝션 목록
   */
  public List<OrderProductVoucherProjection> findOrderProductVouchers(Long orderId) {
    return orderProductVoucherRepository.findAllByOrderProductOrderId(orderId);
  }

  /**
   * 주문 상품 목록에 대해 발급된 바우처 수를 계산합니다.
   *
   * @param orderProducts 바우처 수를 계산할 주문 상품 목록
   * @return 각 주문 상품별 발급된 바우처 수 정보
   */
  public List<OrderProductVoucherCount> countIssuedVouchersByOrderProducts(
      List<OrderProduct> orderProducts) {
    return orderProductVoucherRepository.countIssuedVouchersByOrderProducts(orderProducts);
  }
}
