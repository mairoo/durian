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
import kr.co.pincoin.api.domain.shop.model.order.Order;
import kr.co.pincoin.api.domain.shop.model.order.OrderProduct;
import kr.co.pincoin.api.domain.shop.model.order.condition.OrderSearchCondition;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderCurrency;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderStatus;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderVisibility;
import kr.co.pincoin.api.domain.shop.model.product.Product;
import kr.co.pincoin.api.domain.shop.model.product.enums.ProductStatus;
import kr.co.pincoin.api.domain.shop.model.product.enums.ProductStock;
import kr.co.pincoin.api.global.utils.ClientUtils;
import kr.co.pincoin.api.infra.shop.service.OrderPersistenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderProcessingService {

  private final OrderPersistenceService persistenceService;

  // 주문 조회
  public Page<Order> getOrders(OrderSearchCondition condition, Pageable pageable) {
    return persistenceService.searchOrders(condition, pageable);
  }

  public Order getOrder(Long orderId) {
    return persistenceService.findOrder(orderId);
  }

  public Page<Order> getUserOrders(
      Integer userId, OrderSearchCondition condition, Pageable pageable) {
    return persistenceService.searchUserOrders(userId, condition, pageable);
  }

  public Order getUserOrder(Integer userId, String orderNo) {
    return persistenceService.findUserOrder(userId, orderNo);
  }

  public List<OrderProduct> getUserOrderProducts(String orderNo) {
    return persistenceService.findOrderProductsByOrderNo(orderNo);
  }

  public User getUser(Integer userId) {
    return persistenceService.findUser(userId);
  }

  @Transactional
  public Order createOrderFromCart(User user, CartOrderCreateRequest request,
      ClientUtils.ClientInfo clientInfo) {
    List<Product> products = validateProductsForCartOrder(request.getItems());

    validateCartPrices(products, request.getItems());

    BigDecimal totalListPrice = calculateTotalListPrice(request.getItems());
    BigDecimal totalSellingPrice = calculateTotalSellingPrice(request.getItems());

    Order order = Order.builder()
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

    Order savedOrder = persistenceService.saveAndFlush(order);

    List<OrderProduct> orderProducts = createOrderProductsFromCart(request.getItems(), savedOrder);
    persistenceService.saveOrderProducts(orderProducts);

    return savedOrder;
  }

  @Transactional
  public Order createReorder(Integer userId, String orderNo, ClientUtils.ClientInfo clientInfo) {
    List<OrderProduct> originalOrderProducts =
        persistenceService.findOriginalOrderProducts(orderNo, userId);
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

    Order savedReorder = persistenceService.saveAndFlush(reorder);

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

    persistenceService.saveOrderProducts(newOrderProducts);

    return savedReorder;
  }

  // 주문 상태 관리
  @Transactional
  public void verifyOrder(Order order) {
    validateOrderStatus(
        order,
        Set.of(OrderStatus.PAYMENT_COMPLETED, OrderStatus.UNDER_REVIEW),
        "결제 완료 또는 검토중 상태의 주문만 검증할 수 있습니다.");

    order.updateStatus(OrderStatus.PAYMENT_VERIFIED);
    persistenceService.save(order);
  }

  @Transactional
  public void unverifyOrder(Order order) {
    validateOrderStatus(order, Set.of(OrderStatus.PAYMENT_VERIFIED), "검증 완료 상태의 주문만 변경할 수 있습니다.");

    order.updateStatus(OrderStatus.UNDER_REVIEW);
    persistenceService.save(order);
  }

  @Transactional
  public void updateOrderStatus(Order order, OrderStatus newStatus) {
    order.updateStatus(newStatus);
    persistenceService.save(order);
  }

  @Transactional
  public void softDeleteOrder(Long orderId) {
    Order order = persistenceService.findOrder(orderId);
    validateSoftDelete(order);
    persistenceService.softDeleteOrder(order);
  }

  @Transactional
  public void softDeleteUserOrder(Integer userId, String orderNo) {
    Order order = persistenceService.findUserOrder(userId, orderNo);
    validateSoftDelete(order);
    persistenceService.softDeleteUserOrder(order);
  }

  @Transactional
  public void hideOrder(Long orderId) {
    Order order = persistenceService.findOrder(orderId);
    validateHideOrder(order);
    persistenceService.hideOrder(order);
  }

  @Transactional
  public void hideUserOrder(Integer userId, String orderNo) {
    Order order = persistenceService.findUserOrder(userId, orderNo);
    validateHideOrder(order);
    persistenceService.hideUserOrder(order);
  }

  // Private 헬퍼 메서드들
  private String generateOrderNumber() {
    return UUID.randomUUID().toString().replace("-", "");
  }

  private void validateOrderStatus(Order order, Set<OrderStatus> validStatuses, String message) {
    if (!validStatuses.contains(order.getStatus())) {
      throw new IllegalStateException(message);
    }
  }

  private void validateSoftDelete(Order order) {
    if (Boolean.TRUE.equals(order.getRemoved())) {
      throw new IllegalStateException("이미 삭제된 주문입니다.");
    }
  }

  private void validateHideOrder(Order order) {
    if (OrderVisibility.HIDDEN.equals(order.getVisibility())) {
      throw new IllegalStateException("이미 숨김 처리된 주문입니다.");
    }
  }

  private List<Product> validateProductsForCartOrder(List<CartItem> items) {
    List<String> codes = items.stream()
        .map(CartItem::getCode)
        .distinct()
        .toList();

    List<Product> products = new ArrayList<>(persistenceService.findProductsByCode(codes).values());

    Set<String> requestedCodes = new HashSet<>(codes);
    Set<String> foundCodes = products.stream()
        .map(Product::getCode)
        .collect(Collectors.toSet());

    if (!foundCodes.containsAll(requestedCodes)) {
      Set<String> notFoundCodes = new HashSet<>(requestedCodes);
      notFoundCodes.removeAll(foundCodes);
      throw new EntityNotFoundException("상품을 찾을 수 없습니다: " + String.join(", ", notFoundCodes));
    }

    Map<String, Integer> quantityByCode = items.stream()
        .collect(Collectors.groupingBy(
            CartItem::getCode,
            Collectors.summingInt(CartItem::getQuantity)
        ));

    List<String> errors = new ArrayList<>();
    for (Product product : products) {
      Integer requestedQuantity = quantityByCode.get(product.getCode());
      if (product.getStatus() == ProductStatus.DISABLED
          || product.getStock() == ProductStock.SOLD_OUT) {
        errors.add("판매 중인 상품이 아닙니다: " + product.getCode());
      } else if (product.getStockQuantity() < requestedQuantity) {
        errors.add(String.format(
            "상품 '%s'의 재고가 부족합니다. 요청: %d, 재고: %d",
            product.getName(), requestedQuantity, product.getStockQuantity()
        ));
      }
    }

    if (!errors.isEmpty()) {
      throw new IllegalStateException(String.join("\n", errors));
    }

    return products;
  }

  private void validateCartPrices(List<Product> products, List<CartItem> items) {
    Map<Long, Product> productMap = products.stream()
        .collect(Collectors.toMap(Product::getId, Function.identity()));

    List<String> priceErrors = new ArrayList<>();

    for (CartItem item : items) {
      Product product = productMap.get(item.getProductId());

      if (product.getSellingPrice().compareTo(item.getSellingPrice()) != 0) {
        priceErrors.add(String.format(
            "상품 '%s'의 가격이 변경되었습니다. 장바구니: %s, 실제: %s",
            product.getName(),
            item.getSellingPrice(),
            product.getSellingPrice()
        ));
      }
    }

    if (!priceErrors.isEmpty()) {
      throw new IllegalStateException(String.join("\n", priceErrors));
    }
  }

  private List<OrderProduct> createOrderProductsFromCart(List<CartItem> items, Order order) {
    return items.stream()
        .map(item -> OrderProduct.of(
            item.getName(),
            item.getSubtitle(),
            item.getCode(),
            item.getSellingPrice(),
            item.getSellingPrice(),
            item.getQuantity(),
            order
        ))
        .collect(Collectors.toList());
  }

  private BigDecimal calculateTotalListPrice(List<CartItem> items) {
    return items.stream()
        .map(item -> item.getListPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  private BigDecimal calculateTotalSellingPrice(List<CartItem> items) {
    return items.stream()
        .map(item -> item.getSellingPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }
}
