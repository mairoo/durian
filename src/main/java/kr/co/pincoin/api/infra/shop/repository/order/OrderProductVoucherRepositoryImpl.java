package kr.co.pincoin.api.infra.shop.repository.order;

import java.util.List;
import kr.co.pincoin.api.domain.shop.model.order.OrderProduct;
import kr.co.pincoin.api.domain.shop.model.order.OrderProductVoucher;
import kr.co.pincoin.api.domain.shop.repository.order.OrderProductVoucherRepository;
import kr.co.pincoin.api.infra.shop.entity.order.OrderProductVoucherEntity;
import kr.co.pincoin.api.infra.shop.mapper.order.OrderProductVoucherMapper;
import kr.co.pincoin.api.infra.shop.repository.order.projection.OrderProductVoucherCount;
import kr.co.pincoin.api.infra.shop.repository.order.projection.OrderProductVoucherProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderProductVoucherRepositoryImpl implements OrderProductVoucherRepository {
  private final OrderProductVoucherJpaRepository jpaRepository;
  private final OrderProductVoucherQueryRepository queryRepository;
  private final OrderProductVoucherMapper mapper;

  @Override
  public List<OrderProductVoucher> saveAll(List<OrderProductVoucher> orderProductVouchers) {
    List<OrderProductVoucherEntity> orderProductVoucherEntities =
        mapper.toEntityList(orderProductVouchers);
    List<OrderProductVoucherEntity> savedEntities =
        jpaRepository.saveAll(orderProductVoucherEntities);
    return mapper.toModelList(savedEntities);
  }

  /**
   * 주문 ID로 주문 상품 바우처 목록을 조회합니다
   *
   * @param orderId 주문 ID
   * @return 조회된 주문 상품 바우처 프로젝션 목록
   */
  @Override
  public List<OrderProductVoucherProjection> findAllByOrderProductOrderId(Long orderId) {
    return queryRepository.findAllByOrderProductOrderId(orderId);
  }

  @Override
  public List<OrderProductVoucherCount> countIssuedVouchersByOrderProducts(
      List<OrderProduct> orderProducts) {
    return queryRepository.countIssuedVouchersByOrderProducts(orderProducts);
  }
}
