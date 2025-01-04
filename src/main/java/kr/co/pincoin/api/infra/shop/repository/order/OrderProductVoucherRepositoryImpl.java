package kr.co.pincoin.api.infra.shop.repository.order;

import java.util.List;
import kr.co.pincoin.api.domain.shop.model.order.OrderProduct;
import kr.co.pincoin.api.domain.shop.model.order.OrderProductVoucher;
import kr.co.pincoin.api.domain.shop.repository.order.OrderProductVoucherRepository;
import kr.co.pincoin.api.infra.shop.repository.order.projection.OrderProductVoucherCount;
import kr.co.pincoin.api.infra.shop.repository.order.projection.OrderProductVoucherProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderProductVoucherRepositoryImpl implements OrderProductVoucherRepository {
  private final OrderProductVoucherQueryRepository queryRepository;
  private final OrderProductVoucherJdbcRepository jdbcRepository;

  /**
   * 주문 상품 상품권 목록을 일괄 저장합니다.
   *
   * @param orderProductVouchers 저장할 주문 상품 상품권 목록
   */
  @Override
  public void batchSave(List<OrderProductVoucher> orderProductVouchers) {
    jdbcRepository.batchInsert(orderProductVouchers);
  }

  /**
   * 주문 ID로 주문 상품 상품권 목록을 조회합니다.
   *
   * @param orderId 주문 ID
   * @return 조회된 주문 상품 상품권 프로젝션 목록
   */
  @Override
  public List<OrderProductVoucherProjection> findAllByOrderProductOrderId(Long orderId) {
    return queryRepository.findAllByOrderProductOrderId(orderId);
  }

  /**
   * 주문 상품 목록에 대해 발급된 상품권 수를 계산합니다.
   *
   * @param orderProducts 상품권 수를 계산할 주문 상품 목록
   * @return 각 주문 상품별 발급된 상품권 수 정보
   */
  @Override
  public List<OrderProductVoucherCount> countIssuedVouchersByOrderProducts(
      List<OrderProduct> orderProducts) {
    return queryRepository.countIssuedVouchersByOrderProducts(orderProducts);
  }
}
