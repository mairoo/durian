package kr.co.pincoin.api.infra.shop.repository.order;

import java.util.List;
import kr.co.pincoin.api.domain.shop.model.order.OrderProduct;
import kr.co.pincoin.api.domain.shop.model.order.OrderProductDetached;
import kr.co.pincoin.api.domain.shop.model.order.condition.OrderProductSearchCondition;
import kr.co.pincoin.api.domain.shop.repository.order.OrderProductRepository;
import kr.co.pincoin.api.infra.shop.entity.order.OrderProductEntity;
import kr.co.pincoin.api.infra.shop.mapper.order.OrderProductMapper;
import kr.co.pincoin.api.infra.shop.repository.order.projection.OrderProductProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderProductRepositoryImpl implements OrderProductRepository {
  private final OrderProductQueryRepository queryRepository;
  private final OrderProductJdbcRepository jdbcRepository;
  private final OrderProductMapper mapper;

  @Override
  public void batchInsert(List<OrderProduct> orderProducts) {
    jdbcRepository.batchInsert(orderProducts);
  }

  /**
   * 조건에 맞는 주문 상품 목록을 조회합니다
   *
   * @param condition 검색 조건
   * @return 조회된 주문 상품 목록
   */
  @Override
  public List<OrderProduct> findAll(OrderProductSearchCondition condition) {
    List<OrderProductEntity> entries = queryRepository.findAll(condition);
    return mapper.toModelList(entries);
  }

  /**
   * 조건에 맞는 분리된 주문 상품 목록을 조회합니다
   *
   * @param condition 검색 조건
   * @return 조회된 분리된 주문 상품 목록
   */
  @Override
  public List<OrderProductDetached> findAllDetached(OrderProductSearchCondition condition) {
    return queryRepository.findAllDetached(condition);
  }

  /**
   * 주문 번호와 사용자 ID로 주문 상품 목록을 주문 및 사용자 정보와 함께 조회합니다
   *
   * @param orderNo 주문 번호
   * @param userId 사용자 ID
   * @return 조회된 주문 상품 목록
   */
  @Override
  public List<OrderProduct> findAllWithOrderAndUser(String orderNo, Integer userId) {
    List<OrderProductEntity> entries = queryRepository.findAllWithOrderAndUser(orderNo, userId);
    return mapper.toModelList(entries);
  }

  /**
   * 주문 ID로 주문 상품 목록을 주문, 사용자, 프로필 정보와 함께 조회합니다
   *
   * @param orderId 주문 ID
   * @return 조회된 주문 상품 프로젝션 목록
   */
  @Override
  public List<OrderProductProjection> findAllWithOrderUserProfileByOrderId(Long orderId) {
    return queryRepository.findAllWithOrderUserProfileByOrderId(orderId);
  }

  /**
   * 주문 ID로 주문 상품 목록을 주문 정보와 함께 조회합니다
   *
   * @param orderId 주문 ID
   * @return 조회된 주문 상품 프로젝션 목록
   */
  @Override
  public List<OrderProductProjection> findAllWithOrderByOrderId(Long orderId) {
    return queryRepository.findAllWithOrderByOrderId(orderId);
  }
}
