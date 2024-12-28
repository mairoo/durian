package kr.co.pincoin.api.infra.shop.service;

import java.util.List;
import kr.co.pincoin.api.domain.shop.model.order.OrderProductVoucher;
import kr.co.pincoin.api.domain.shop.repository.order.OrderProductVoucherRepository;
import kr.co.pincoin.api.infra.shop.repository.order.projection.OrderProductVoucherProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderProductVoucherPersistenceService {

  private final OrderProductVoucherRepository orderProductVoucherRepository;

  /** 주문 ID로 주문상품 바우처 목록 조회 */
  public List<OrderProductVoucherProjection> findOrderProductVouchers(Long orderId) {
    return orderProductVoucherRepository.findAllByOrderProductOrderId(orderId);
  }

  /** 주문상품 바우처 목록 저장 */
  @Transactional
  public void saveOrderProductVouchers(List<OrderProductVoucher> vouchers) {
    orderProductVoucherRepository.saveAll(vouchers);
  }

  /** 주문상품 바우처 목록 일괄 저장 (배치) */
  @Transactional
  public void saveOrderProductVouchersBatch(List<OrderProductVoucher> vouchers) {
    int batchSize = 100;
    for (int i = 0; i < vouchers.size(); i += batchSize) {
      List<OrderProductVoucher> batch =
          vouchers.subList(i, Math.min(i + batchSize, vouchers.size()));
      orderProductVoucherRepository.saveAll(batch);
    }
  }
}
