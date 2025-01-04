package kr.co.pincoin.api.infra.shop.service;

import java.util.List;
import java.util.stream.Collectors;
import kr.co.pincoin.api.domain.shop.model.order.Order;
import kr.co.pincoin.api.domain.shop.model.order.OrderProduct;
import kr.co.pincoin.api.domain.shop.model.order.OrderProductDetached;
import kr.co.pincoin.api.domain.shop.model.order.condition.OrderProductSearchCondition;
import kr.co.pincoin.api.domain.shop.repository.order.OrderProductRepository;
import kr.co.pincoin.api.infra.shop.mapper.order.OrderProductMapper;
import kr.co.pincoin.api.infra.shop.repository.order.projection.OrderProductProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderProductPersistenceService {
  private final OrderProductRepository orderProductRepository;
  private final OrderProductMapper orderProductMapper;

  /**
   * 주문상품 목록을 일괄 저장합니다.
   *
   * @param orderProducts 저장할 주문상품 목록
   */
  @Transactional
  public void batchSave(List<OrderProduct> orderProducts) {
    orderProductRepository.batchInsert(orderProducts);
  }

  /**
   * 주문 ID로 주문상품 목록을 조회합니다.
   *
   * @param orderId 주문 ID
   * @return 주문상품 목록
   */
  public List<OrderProduct> findOrderProductsByOrderId(Long orderId) {
    return orderProductRepository.findAll(OrderProductSearchCondition.ofOrderId(orderId));
  }

  /**
   * 사용자 ID와 주문번호로 주문상품 목록을 조회합니다.
   *
   * @param userId 사용자 ID
   * @param orderNo 주문번호
   * @return 주문상품 목록
   */
  public List<OrderProduct> findOrderProductsByUserIdAndOrderNo(Integer userId, String orderNo) {
    return orderProductRepository.findAll(
        OrderProductSearchCondition.ofOrderNo(orderNo).withUserId(userId));
  }

  /**
   * 주문 ID로 주문상품 목록을 조회합니다. (사용자 프로필 정보 포함)
   *
   * @param orderId 주문 ID
   * @return 주문상품 projection 목록
   */
  public List<OrderProductProjection> findAllWithOrderUserProfileByOrderId(Long orderId) {
    return orderProductRepository.findAllWithOrderUserProfileByOrderId(orderId);
  }

  /**
   * 사용자 ID와 주문번호로 분리된 주문상품 목록을 조회합니다.
   *
   * @param userId 사용자 ID
   * @param orderNo 주문번호
   * @return 분리된 주문상품 목록
   */
  public List<OrderProductDetached> findOrderProductsDetachedByUserIdAndOrderNo(
      Integer userId, String orderNo) {
    return orderProductRepository.findAllDetached(
        OrderProductSearchCondition.ofOrderNo(orderNo).withUserId(userId));
  }

  /**
   * 주문에 속한 주문상품 목록을 조회합니다.
   *
   * @param order 주문 정보
   * @return 주문상품 목록
   */
  public List<OrderProduct> findOrderProductsWithOrder(Order order) {
    List<OrderProductProjection> projections =
        orderProductRepository.findAllWithOrderByOrderId(order.getId());

    return projections.stream().map(orderProductMapper::mapToDomain).collect(Collectors.toList());
  }

  /**
   * 원본 주문상품 목록을 조회합니다. (사용자 정보 포함)
   *
   * @param orderNo 주문번호
   * @param userId 사용자 ID
   * @return 주문상품 목록
   */
  public List<OrderProduct> findOriginalOrderProducts(String orderNo, Integer userId) {
    return orderProductRepository.findAllWithOrderAndUser(orderNo, userId);
  }
}
