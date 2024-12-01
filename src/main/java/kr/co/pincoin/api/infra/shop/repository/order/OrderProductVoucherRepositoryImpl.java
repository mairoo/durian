package kr.co.pincoin.api.infra.shop.repository.order;

import java.util.List;
import kr.co.pincoin.api.domain.shop.model.order.OrderProductVoucher;
import kr.co.pincoin.api.domain.shop.repository.order.OrderProductVoucherRepository;
import kr.co.pincoin.api.infra.shop.entity.order.OrderProductVoucherEntity;
import kr.co.pincoin.api.infra.shop.mapper.order.OrderProductVoucherMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderProductVoucherRepositoryImpl implements OrderProductVoucherRepository {

  private final OrderProductVoucherJpaRepository jpaRepository;

  private final OrderProductVoucherQueryRepository queryRepository;

  private final OrderProductVoucherMapper mapper;

  @Override
  public List<OrderProductVoucher> saveAll(List<OrderProductVoucher> orderProductsVouchers) {
    // 1. Domain Model -> JPA Entity 변환
    List<OrderProductVoucherEntity> orderProductEntities =
        mapper.toEntityList(orderProductsVouchers);

    // 2. JPA Repository 일괄 저장
    List<OrderProductVoucherEntity> savedEntities =
        jpaRepository.saveAll(orderProductEntities);

    // 3. 저장된 Entity -> Domain Model 변환 후 반환
    return mapper.toModelList(savedEntities);
  }

  @Override
  public List<OrderProductVoucher> findAllByOrderProductOrderId(Long orderId) {
    List<OrderProductVoucherEntity> entries =
        jpaRepository.findAllByOrderProductOrderId(orderId);

    return mapper.toModelList(entries);
  }
}
