package kr.co.pincoin.api.infra.shop.repository.order;

import java.util.List;
import kr.co.pincoin.api.domain.shop.model.order.Order;
import kr.co.pincoin.api.domain.shop.model.order.OrderProduct;
import kr.co.pincoin.api.domain.shop.repository.order.OrderProductRepository;
import kr.co.pincoin.api.infra.shop.entity.order.OrderProductEntity;
import kr.co.pincoin.api.infra.shop.mapper.order.OrderProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderProductRepositoryImpl implements OrderProductRepository {
  private final OrderProductJpaRepository orderProductJpaRepository;

  private final OrderProductQueryRepository orderProductQueryRepository;

  private final OrderProductMapper orderProductMapper;

  @Override
  public List<OrderProduct> saveAll(List<OrderProduct> orderProducts) {
    // 1. Domain Model -> JPA Entity 변환
    List<OrderProductEntity> orderProductEntities = orderProductMapper.toEntityList(orderProducts);

    // 2. JPA Repository 일괄 저장
    List<OrderProductEntity> savedEntities =
        orderProductJpaRepository.saveAll(orderProductEntities);

    // 3. 저장된 Entity -> Domain Model 변환 후 반환
    return orderProductMapper.toModelList(savedEntities);
  }

  @Override
  public List<OrderProduct> findAllByOrderNoAndUserIdFetchOrder(
      String orderNo, Integer userId) {
    List<OrderProductEntity> entries =
        orderProductJpaRepository.findAllByOrderNoAndUserIdFetchOrder(orderNo, userId);

    return orderProductMapper.toModelList(entries);
  }

  @Override
  public List<OrderProduct> findAllByOrderFetchOrder(Order order) {
    List<OrderProductEntity> entries = orderProductJpaRepository.findAllByOrderFetchOrder(order);

    return orderProductMapper.toModelList(entries);
  }
}
