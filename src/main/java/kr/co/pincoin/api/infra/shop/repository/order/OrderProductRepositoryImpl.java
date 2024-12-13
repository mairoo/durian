package kr.co.pincoin.api.infra.shop.repository.order;

import java.util.List;
import kr.co.pincoin.api.domain.shop.model.order.Order;
import kr.co.pincoin.api.domain.shop.model.order.OrderProduct;
import kr.co.pincoin.api.domain.shop.model.order.condition.OrderProductSearchCondition;
import kr.co.pincoin.api.domain.shop.repository.order.OrderProductRepository;
import kr.co.pincoin.api.infra.shop.entity.order.OrderProductEntity;
import kr.co.pincoin.api.infra.shop.mapper.order.OrderProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderProductRepositoryImpl implements OrderProductRepository {

  private final OrderProductJpaRepository jpaRepository;

  private final OrderProductQueryRepository queryRepository;

  private final OrderProductMapper mapper;

  @Override
  public List<OrderProduct> saveAll(List<OrderProduct> orderProducts) {
    // 1. Domain Model -> JPA Entity 변환
    List<OrderProductEntity> orderProductEntities = mapper.toEntityList(orderProducts);

    // 2. JPA Repository 일괄 저장
    List<OrderProductEntity> savedEntities = jpaRepository.saveAll(orderProductEntities);

    // 3. 저장된 Entity -> Domain Model 변환 후 반환
    return mapper.toModelList(savedEntities);
  }

  @Override
  public List<OrderProduct> findAll(OrderProductSearchCondition condition) {
    List<OrderProductEntity> entries = queryRepository.findAll(condition);

    return mapper.toModelList(entries);
  }

  @Override
  public List<OrderProduct> findAllWithOrderAndUser(String orderNo, Integer userId) {
    List<OrderProductEntity> entries =
        queryRepository.findAllWithOrderAndUser(orderNo, userId);

    return mapper.toModelList(entries);
  }

  @Override
  public List<OrderProduct> findAllWithOrder(Order order) {
    List<OrderProductEntity> entries = queryRepository.findAllWithOrder(order.toEntity());

    return mapper.toModelList(entries);
  }
}
