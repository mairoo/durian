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

  /**
   * 주문 상품 목록을 데이터베이스에 일괄 삽입합니다. JDBC batch update를 사용하여 대량의 데이터를 효율적으로 처리합니다.
   *
   * @param orderProducts 저장할 주문 상품 목록
   */
  @Override
  public void batchInsert(List<OrderProduct> orderProducts) {
    jdbcRepository.batchInsert(orderProducts);
  }

  /**
   * 검색 조건에 맞는 주문 상품 목록을 조회합니다.
   *
   * @param condition 검색 조건 (주문 ID, 주문 번호, 사용자 ID 등)
   * @return 조회된 주문 상품 목록
   */
  @Override
  public List<OrderProduct> findAll(OrderProductSearchCondition condition) {
    List<OrderProductEntity> entries = queryRepository.findAll(condition);
    return mapper.toModelList(entries);
  }

  /**
   * 검색 조건에 맞는 분리된 주문 상품 목록을 조회합니다. 연관된 엔티티와의 조인 없이 필요한 데이터만 조회합니다.
   *
   * @param condition 검색 조건 (주문 ID, 주문 번호)
   * @return 조회된 분리된 주문 상품 목록
   */
  @Override
  public List<OrderProductDetached> findAllDetached(OrderProductSearchCondition condition) {
    return queryRepository.findAllDetached(condition);
  }

  /**
   * 주문 번호와 사용자 ID로 주문 상품 목록을 조회합니다. 연관된 주문과 사용자 정보를 함께 조회합니다.
   *
   * @param orderNo 주문 번호
   * @param userId 사용자 ID
   * @return 주문과 사용자 정보가 포함된 주문 상품 목록
   */
  @Override
  public List<OrderProduct> findAllWithOrderAndUser(String orderNo, Integer userId) {
    List<OrderProductEntity> entries = queryRepository.findAllWithOrderAndUser(orderNo, userId);
    return mapper.toModelList(entries);
  }

  /**
   * 주문 ID로 주문 상품 목록을 조회합니다. 연관된 주문, 사용자, 프로필 정보를 함께 조회합니다.
   *
   * @param orderId 주문 ID
   * @return 주문, 사용자, 프로필 정보가 포함된 주문 상품 프로젝션 목록
   */
  @Override
  public List<OrderProductProjection> findAllWithOrderUserProfileByOrderId(Long orderId) {
    return queryRepository.findAllWithOrderUserProfileByOrderId(orderId);
  }

  /**
   * 주문 ID로 주문 상품 목록을 조회합니다. 연관된 주문 정보를 함께 조회합니다.
   *
   * @param orderId 주문 ID
   * @return 주문 정보가 포함된 주문 상품 프로젝션 목록
   */
  @Override
  public List<OrderProductProjection> findAllWithOrderByOrderId(Long orderId) {
    return queryRepository.findAllWithOrderByOrderId(orderId);
  }
}
