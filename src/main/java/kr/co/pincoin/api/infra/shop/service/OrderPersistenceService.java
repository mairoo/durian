package kr.co.pincoin.api.infra.shop.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import kr.co.pincoin.api.domain.shop.model.order.Order;
import kr.co.pincoin.api.domain.shop.model.order.condition.OrderSearchCondition;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderVisibility;
import kr.co.pincoin.api.domain.shop.repository.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderPersistenceService {

  private final OrderRepository orderRepository;

  /** 주문 ID로 주문 엔티티 조회 */
  public Order findOrder(Long orderId) {
    return orderRepository
        .findById(orderId)
        .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다."));
  }

  /** 주문 ID로 사용자 정보가 포함된 주문 조회 */
  public Order findOrderWithUser(Long orderId) {
    return orderRepository
        .findByIdWithUser(orderId)
        .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다."));
  }

  /** 사용자 ID와 주문번호로 주문 조회 */
  public Order findUserOrder(Integer userId, String orderNo) {
    return orderRepository
        .findByOrderNoAndUserId(orderNo, userId)
        .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다."));
  }

  /** 주문 검색 조건으로 주문 목록 페이징 조회 */
  public Page<Order> searchOrders(OrderSearchCondition condition, Pageable pageable) {
    OrderSearchCondition searchCondition =
        Optional.ofNullable(condition).orElseGet(OrderSearchCondition::empty);

    Pageable pageRequest = Optional.ofNullable(pageable).orElse(Pageable.unpaged());

    return orderRepository.searchOrders(searchCondition, pageRequest);
  }

  /** 사용자 주문 검색 조건으로 주문 목록 페이징 조회 */
  public Page<Order> searchUserOrders(OrderSearchCondition condition, Pageable pageable) {
    return searchOrders(
        Optional.ofNullable(condition).orElseGet(OrderSearchCondition::empty), pageable);
  }

  /** 주문 저장 */
  @Transactional
  public Order save(Order order) {
    return orderRepository.save(order);
  }

  /** 주문 저장 및 즉시 반영 */
  @Transactional
  public Order saveAndFlush(Order order) {
    return orderRepository.saveAndFlush(order);
  }

  /** 원본 주문과 환불 주문 일괄 저장 */
  @Transactional
  public void saveOrders(Order originalOrder, Order refundOrder) {
    orderRepository.saveAll(List.of(originalOrder, refundOrder));
  }

  /** 주문 소프트 삭제 */
  @Transactional
  public void softDeleteOrder(Order order) {
    order.softDelete();
    save(order);
  }

  /** 주문 숨김 처리 */
  @Transactional
  public void hideOrder(Order order) {
    order.updateVisibility(OrderVisibility.HIDDEN);
    save(order);
  }

  /** 사용자 주문 숨김 처리 */
  @Transactional
  public void hideUserOrder(Order order) {
    order.updateVisibility(OrderVisibility.HIDDEN);
    save(order);
  }
}
