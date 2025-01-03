package kr.co.pincoin.api.infra.shop.repository.order;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import kr.co.pincoin.api.domain.shop.model.order.OrderProduct;
import kr.co.pincoin.api.domain.shop.model.order.OrderProductVoucher;
import kr.co.pincoin.api.domain.shop.repository.order.OrderProductVoucherRepository;
import kr.co.pincoin.api.infra.shop.entity.order.OrderProductEntity;
import kr.co.pincoin.api.infra.shop.entity.order.OrderProductVoucherEntity;
import kr.co.pincoin.api.infra.shop.mapper.order.OrderProductMapper;
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
  private final OrderProductMapper orderProductMapper;

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

  @Override
  public List<OrderProductVoucher> saveAllWithMap(
      List<OrderProductVoucher> orderProductVouchers,
      Map<Long, OrderProductEntity> orderProductMap) {

    // 1. Model -> Entity 변환 (voucher 연결)
    List<OrderProductVoucherEntity> entities =
        orderProductVouchers.stream()
            .map(
                voucher -> {
                  OrderProductVoucherEntity entity = mapper.toEntity(voucher);
                  // OrderProduct 연결
                  entity.setOrderProduct(orderProductMap.get(voucher.getOrderProduct().getId()));
                  return entity;
                })
            .collect(Collectors.toList());

    // 2. Entity 저장
    List<OrderProductVoucherEntity> savedEntities = jpaRepository.saveAll(entities);

    // 3. 데이터 검증
    savedEntities.forEach(
        entity -> {
          if (entity.getVoucher() != null
              && (entity.getVoucher().getCode() == null
                  || entity.getVoucher().getCode().trim().isEmpty())) {
            throw new IllegalStateException(
                String.format(
                    "Invalid voucher data: Voucher code is empty for voucher ID: %d",
                    entity.getVoucher().getId()));
          }
        });

    // 4. 저장된 엔티티를 캐시된 OrderProduct 정보와 함께 모델로 변환
    return savedEntities.stream()
        .map(
            entity ->
                mapper.toModel(
                    entity,
                    orderProductMapper.toModel(
                        orderProductMap.get(entity.getOrderProduct().getId()))))
        .collect(Collectors.toList());
  }
}
