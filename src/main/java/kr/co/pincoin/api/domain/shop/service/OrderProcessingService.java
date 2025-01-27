package kr.co.pincoin.api.domain.shop.service;

import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import kr.co.pincoin.api.app.member.order.request.CartItem;
import kr.co.pincoin.api.app.member.order.request.CartOrderCreateRequest;
import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.domain.shop.event.order.OrderCreatedEvent;
import kr.co.pincoin.api.domain.shop.model.order.Order;
import kr.co.pincoin.api.domain.shop.model.order.OrderProduct;
import kr.co.pincoin.api.domain.shop.model.order.OrderProductDetached;
import kr.co.pincoin.api.domain.shop.model.order.condition.OrderSearchCondition;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderCurrency;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderStatus;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderVisibility;
import kr.co.pincoin.api.domain.shop.model.product.ProductDetached;
import kr.co.pincoin.api.domain.shop.model.product.enums.ProductStatus;
import kr.co.pincoin.api.domain.shop.model.product.enums.ProductStock;
import kr.co.pincoin.api.global.exception.BusinessException;
import kr.co.pincoin.api.global.exception.ErrorCode;
import kr.co.pincoin.api.global.utils.ClientUtils;
import kr.co.pincoin.api.infra.auth.service.UserProfilePersistenceService;
import kr.co.pincoin.api.infra.shop.repository.order.projection.OrderProductVoucherProjection;
import kr.co.pincoin.api.infra.shop.service.CatalogPersistenceService;
import kr.co.pincoin.api.infra.shop.service.OrderPersistenceService;
import kr.co.pincoin.api.infra.shop.service.OrderProductPersistenceService;
import kr.co.pincoin.api.infra.shop.service.OrderProductVoucherPersistenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class OrderProcessingService {
  private final UserProfilePersistenceService userProfilePersistenceService;

  private final OrderPersistenceService orderPersistenceService;

  private final OrderProductPersistenceService orderProductPersistenceService;

  private final OrderProductVoucherPersistenceService orderProductVoucherPersistenceService;

  private final CatalogPersistenceService catalogPersistenceService;

  private final ApplicationEventPublisher eventPublisher;

  /**
   * 장바구니에서 주문을 생성합니다.
   *
   * @param user 주문하는 사용자
   * @param request 장바구니 주문 생성 요청 정보
   * @param clientInfo 클라이언트 정보
   * @return 생성된 주문
   * @throws BusinessException 상품을 찾을 수 없거나, 재고가 부족하거나, 가격이 변경된 경우 발생
   */
  @Transactional
  public Order createOrderFromCart(
      User user, CartOrderCreateRequest request, ClientUtils.ClientInfo clientInfo) {
    List<ProductDetached> products = validateProductsForCartOrder(request.getItems());

    validateCartPrices(products, request.getItems());

    BigDecimal totalListPrice = calculateTotalListPrice(request.getItems());
    BigDecimal totalSellingPrice = calculateTotalSellingPrice(request.getItems());

    Order order =
        Order.builder()
            .orderNo(generateOrderNumber())
            .fullname(user.getLastName() + user.getFirstName())
            .userAgent(clientInfo.getUserAgent())
            .acceptLanguage(clientInfo.getAcceptLanguage())
            .ipAddress(clientInfo.getIpAddress())
            .totalListPrice(totalListPrice)
            .totalSellingPrice(totalSellingPrice)
            .currency(OrderCurrency.KRW)
            .parent(null)
            .user(user)
            .paymentMethod(request.getPaymentMethod())
            .status(OrderStatus.PAYMENT_PENDING)
            .visibility(OrderVisibility.VISIBLE)
            .transactionId("")
            .message("")
            .suspicious(false)
            .removed(false)
            .build();

    Order savedOrder = orderPersistenceService.saveAndFlush(order);

    List<OrderProduct> orderProducts = createOrderProductsFromCart(request.getItems(), savedOrder);
    orderProductPersistenceService.batchSave(orderProducts);

    eventPublisher.publishEvent(new OrderCreatedEvent(savedOrder, orderProducts));

    return savedOrder;
  }

  /**
   * 기존 주문을 기반으로 재주문을 생성합니다.
   *
   * @param userId 사용자 ID
   * @param orderNo 원본 주문 번호
   * @param clientInfo 클라이언트 정보
   * @return 생성된 재주문
   * @throws EntityNotFoundException 원본 주문을 찾을 수 없는 경우 발생
   */
  @Transactional
  public Order createReorder(Integer userId, String orderNo, ClientUtils.ClientInfo clientInfo) {
    List<OrderProduct> originalOrderProducts =
        orderProductPersistenceService.findOriginalOrderProducts(orderNo, userId);
    if (originalOrderProducts.isEmpty()) {
      throw new EntityNotFoundException("주문을 찾을 수 없습니다.");
    }

    Order originalOrder = originalOrderProducts.getFirst().getOrder();
    Order reorder =
        Order.builder()
            .orderNo(generateOrderNumber())
            .fullname(originalOrder.getFullname())
            .userAgent(clientInfo.getUserAgent())
            .acceptLanguage(clientInfo.getAcceptLanguage())
            .ipAddress(clientInfo.getIpAddress())
            .totalListPrice(originalOrder.getTotalListPrice())
            .totalSellingPrice(originalOrder.getTotalSellingPrice())
            .currency(originalOrder.getCurrency())
            .user(originalOrder.getUser())
            .paymentMethod(originalOrder.getPaymentMethod())
            .status(OrderStatus.PAYMENT_PENDING)
            .visibility(OrderVisibility.VISIBLE)
            .transactionId("")
            .message("")
            .suspicious(false)
            .removed(false)
            .build();

    Order savedReorder = orderPersistenceService.saveAndFlush(reorder);

    List<OrderProduct> newOrderProducts =
        originalOrderProducts.stream()
            .map(
                original ->
                    OrderProduct.of(
                        original.getName(),
                        original.getSubtitle(),
                        original.getCode(),
                        original.getListPrice(),
                        original.getSellingPrice(),
                        original.getQuantity(),
                        savedReorder))
            .collect(Collectors.toList());

    orderProductPersistenceService.batchSave(newOrderProducts);

    return savedReorder;
  }

  /**
   * 조건에 따라 주문 목록을 페이징하여 조회합니다.
   *
   * @param condition 검색 조건
   * @param pageable 페이징 정보
   * @return 주문 목록 페이지
   */
  public Page<Order> getOrders(OrderSearchCondition condition, Pageable pageable) {
    return orderPersistenceService.searchOrders(condition, pageable);
  }

  /**
   * 주문 ID로 주문을 조회합니다.
   *
   * @param orderId 주문 ID
   * @return 주문 정보
   */
  public Order getOrder(Long orderId) {
    return orderPersistenceService.findOrder(orderId);
  }

  /**
   * 사용자의 주문을 주문번호로 조회합니다.
   *
   * @param userId 사용자 ID
   * @param orderNo 주문 번호
   * @return 주문 정보
   */
  public Order getUserOrder(Integer userId, String orderNo) {
    return orderPersistenceService.findUserOrder(userId, orderNo);
  }

  /**
   * 사용자의 주문 상품 목록을 조회합니다.
   *
   * @param user 사용자
   * @param orderNo 주문 번호
   * @return 주문 상품 목록
   */
  public List<OrderProductDetached> getUserOrderProductsDetached(User user, String orderNo) {
    return orderProductPersistenceService.findOrderProductsDetachedByUserIdAndOrderNo(
        user.getId(), orderNo);
  }

  /**
   * 주문의 상품별 바우처 목록을 조회합니다.
   *
   * @param orderId 주문 ID
   * @return 주문 상품별 바우처 목록
   */
  public List<OrderProductVoucherProjection> findOrderProductVouchers(Long orderId) {
    return orderProductVoucherPersistenceService.findOrderProductVouchers(orderId);
  }

  /**
   * 사용자 정보를 조회합니다.
   *
   * @param userId 사용자 ID
   * @return 사용자 정보
   */
  public User getUser(Integer userId) {
    return userProfilePersistenceService.findUser(userId);
  }

  /**
   * 주문을 검증 완료 상태로 변경합니다.
   *
   * @param order 대상 주문
   * @throws IllegalStateException 주문 상태가 적절하지 않은 경우 발생
   */
  @Transactional
  public void verifyOrder(Order order) {
    validateOrderStatus(
        order,
        Set.of(OrderStatus.PAYMENT_COMPLETED, OrderStatus.UNDER_REVIEW),
        "결제 완료 또는 검토중 상태의 주문만 검증할 수 있습니다.");

    order.updateStatus(OrderStatus.PAYMENT_VERIFIED);
    orderPersistenceService.save(order);
  }

  /**
   * 주문을 검증 해제 상태로 변경합니다.
   *
   * @param order 대상 주문
   * @throws IllegalStateException 주문 상태가 적절하지 않은 경우 발생
   */
  @Transactional
  public void unverifyOrder(Order order) {
    validateOrderStatus(order, Set.of(OrderStatus.PAYMENT_VERIFIED), "검증 완료 상태의 주문만 변경할 수 있습니다.");

    order.updateStatus(OrderStatus.UNDER_REVIEW);
    orderPersistenceService.save(order);
  }

  /**
   * 주문 상태를 업데이트합니다.
   *
   * @param order 대상 주문
   * @param newStatus 새로운 상태
   */
  @Transactional
  public void updateOrderStatus(Order order, OrderStatus newStatus) {
    order.updateStatus(newStatus);
    orderPersistenceService.save(order);
  }

  /**
   * 주문을 소프트 삭제합니다.
   *
   * @param orderId 주문 ID
   * @throws IllegalStateException 이미 삭제된 주문인 경우 발생
   */
  @Transactional
  public void softDeleteOrder(Long orderId) {
    Order order = orderPersistenceService.findOrder(orderId);
    validateSoftDelete(order);
    orderPersistenceService.softDeleteOrder(order);
  }

  /**
   * 사용자의 주문을 소프트 삭제합니다.
   *
   * @param userId 사용자 ID
   * @param orderNo 주문 번호
   * @throws IllegalStateException 이미 삭제된 주문인 경우 발생
   */
  @Transactional
  public void softDeleteUserOrder(Integer userId, String orderNo) {
    Order order = orderPersistenceService.findUserOrder(userId, orderNo);
    validateSoftDelete(order);
    orderPersistenceService.softDeleteOrder(order);
  }

  /**
   * 주문을 숨김 처리합니다.
   *
   * @param orderId 주문 ID
   * @throws IllegalStateException 이미 숨김 처리된 주문인 경우 발생
   */
  @Transactional
  public void hideOrder(Long orderId) {
    Order order = orderPersistenceService.findOrder(orderId);
    validateHideOrder(order);
    orderPersistenceService.hideOrder(order);
  }

  /**
   * 사용자의 주문을 숨김 처리합니다.
   *
   * @param userId 사용자 ID
   * @param orderNo 주문 번호
   * @throws IllegalStateException 이미 숨김 처리된 주문인 경우 발생
   */
  @Transactional
  public void hideUserOrder(Integer userId, String orderNo) {
    Order order = orderPersistenceService.findUserOrder(userId, orderNo);
    validateHideOrder(order);
    orderPersistenceService.hideUserOrder(order);
  }

  /**
   * 주문 번호를 생성합니다.
   *
   * @return 생성된 주문 번호
   */
  private String generateOrderNumber() {
    return UUID.randomUUID().toString().replace("-", "");
  }

  /**
   * 주문 상태의 유효성을 검사합니다.
   *
   * @param order 대상 주문
   * @param validStatuses 유효한 상태 목록
   * @param message 오류 메시지
   * @throws IllegalStateException 주문 상태가 유효하지 않은 경우 발생
   */
  private void validateOrderStatus(Order order, Set<OrderStatus> validStatuses, String message) {
    if (!validStatuses.contains(order.getStatus())) {
      throw new IllegalStateException(message);
    }
  }

  /**
   * 주문의 소프트 삭제 가능 여부를 검증합니다.
   *
   * @param order 검증할 주문
   * @throws IllegalStateException 이미 삭제된 주문인 경우 발생
   */
  private void validateSoftDelete(Order order) {
    if (Boolean.TRUE.equals(order.getRemoved())) {
      throw new IllegalStateException("이미 삭제된 주문입니다.");
    }
  }

  /**
   * 주문의 숨김 처리 가능 여부를 검증합니다.
   *
   * @param order 검증할 주문
   * @throws IllegalStateException 이미 숨김 처리된 주문인 경우 발생
   */
  private void validateHideOrder(Order order) {
    if (OrderVisibility.HIDDEN.equals(order.getVisibility())) {
      throw new IllegalStateException("이미 숨김 처리된 주문입니다.");
    }
  }

  /**
   * 장바구니 상품들의 유효성을 검증합니다. 상품의 존재 여부, 판매 상태, 재고 수량을 확인합니다.
   *
   * @param items 장바구니 상품 목록
   * @return 검증된 상품 목록
   * @throws BusinessException 상품을 찾을 수 없는 경우 발생
   * @throws IllegalStateException 상품이 판매 중지되었거나 재고가 부족한 경우 발생
   */
  private List<ProductDetached> validateProductsForCartOrder(List<CartItem> items) {
    // 장바구니에 담긴 상품권 권종 목록 가져오기
    List<String> codes = items.stream().map(CartItem::getCode).distinct().toList();

    // 실제 데이터베이스에 존재하는 상품권 권종 목록 가져오기
    List<ProductDetached> products =
        new ArrayList<>(catalogPersistenceService.findProductsDetachedByCodeIn(codes).values());

    Set<String> requestedCodes = new HashSet<>(codes);
    Set<String> foundCodes =
        products.stream().map(ProductDetached::getCode).collect(Collectors.toSet());

    for (String foundCode : foundCodes) {
      log.warn(foundCode);
    }

    if (!foundCodes.containsAll(requestedCodes)) {
      Set<String> notFoundCodes = new HashSet<>(requestedCodes);
      notFoundCodes.removeAll(foundCodes);
      throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, String.join(", ", notFoundCodes));
    }

    Map<String, Integer> quantityByCode =
        items.stream()
            .collect(
                Collectors.groupingBy(
                    CartItem::getCode, Collectors.summingInt(CartItem::getQuantity)));

    List<String> errors = new ArrayList<>();
    for (ProductDetached product : products) {
      Integer requestedQuantity = quantityByCode.get(product.getCode());
      if (product.getStatus() == ProductStatus.DISABLED
          || product.getStock() == ProductStock.SOLD_OUT) {
        errors.add("판매 중인 상품이 아닙니다: " + product.getCode());
      } else if (product.getStockQuantity() < requestedQuantity) {
        errors.add(
            String.format(
                "상품 '%s'의 재고가 부족합니다. 요청: %d, 재고: %d",
                product.getName(), requestedQuantity, product.getStockQuantity()));
      }
    }

    if (!errors.isEmpty()) {
      throw new IllegalStateException(String.join("\n", errors));
    }

    return products;
  }

  /**
   * 장바구니 상품들의 가격 변동 여부를 검증합니다.
   *
   * @param products 실제 상품 목록
   * @param items 장바구니 상품 목록
   * @throws IllegalStateException 상품의 가격이 변경된 경우 발생
   */
  private void validateCartPrices(List<ProductDetached> products, List<CartItem> items) {
    Map<Long, ProductDetached> productMap =
        products.stream().collect(Collectors.toMap(ProductDetached::getId, Function.identity()));

    List<String> priceErrors = new ArrayList<>();

    for (CartItem item : items) {
      ProductDetached product = productMap.get(item.getProductId());

      if (product.getSellingPrice().compareTo(item.getSellingPrice()) != 0) {
        priceErrors.add(
            String.format(
                "상품 '%s'의 가격이 변경되었습니다. 장바구니: %s, 실제: %s",
                product.getName(), item.getSellingPrice(), product.getSellingPrice()));
      }
    }

    if (!priceErrors.isEmpty()) {
      throw new IllegalStateException(String.join("\n", priceErrors));
    }
  }

  /**
   * 장바구니 상품들을 주문 상품으로 변환합니다.
   *
   * @param items 장바구니 상품 목록
   * @param order 연결될 주문
   * @return 생성된 주문 상품 목록
   */
  private List<OrderProduct> createOrderProductsFromCart(List<CartItem> items, Order order) {
    return items.stream()
        .map(
            item ->
                OrderProduct.of(
                    item.getName(),
                    item.getSubtitle(),
                    item.getCode(),
                    item.getListPrice(),
                    item.getSellingPrice(),
                    item.getQuantity(),
                    order))
        .collect(Collectors.toList());
  }

  /**
   * 장바구니 상품들의 총 정가를 계산합니다.
   *
   * @param items 장바구니 상품 목록
   * @return 총 정가
   */
  private BigDecimal calculateTotalListPrice(List<CartItem> items) {
    return items.stream()
        .map(item -> item.getListPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  /**
   * 장바구니 상품들의 총 판매가를 계산합니다.
   *
   * @param items 장바구니 상품 목록
   * @return 총 판매가
   */
  private BigDecimal calculateTotalSellingPrice(List<CartItem> items) {
    return items.stream()
        .map(item -> item.getSellingPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }
}
