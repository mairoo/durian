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
import kr.co.pincoin.api.app.member.order.request.OrderCreateRequest;
import kr.co.pincoin.api.app.member.order.request.OrderLineItem;
import kr.co.pincoin.api.domain.auth.model.profile.Profile;
import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.domain.shop.model.order.Order;
import kr.co.pincoin.api.domain.shop.model.order.OrderPayment;
import kr.co.pincoin.api.domain.shop.model.order.OrderProduct;
import kr.co.pincoin.api.domain.shop.model.order.OrderProductVoucher;
import kr.co.pincoin.api.domain.shop.model.order.condition.OrderSearchCondition;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderCurrency;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderStatus;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderVisibility;
import kr.co.pincoin.api.domain.shop.model.product.Product;
import kr.co.pincoin.api.domain.shop.model.product.Voucher;
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
public class OrderDomainService {
  private final OrderPersistenceService persistenceService;

  /** 주문 조회 */
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

  public User getUser(Integer userId) {
    return persistenceService.findUser(userId);
  }

  /** 주문 생성 */
  @Transactional
  public Order createOrder(
      User user, OrderCreateRequest request, ClientUtils.ClientInfo clientInfo) {
    List<Product> products = validateProductsForOrder(request.getItems());

    BigDecimal totalListPrice =
        calculateTotalPrice(products, request.getItems(), Product::getListPrice);

    BigDecimal totalSellingPrice =
        calculateTotalPrice(products, request.getItems(), Product::getSellingPrice);

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

    Order savedOrder = persistenceService.saveAndFlush(order);

    List<OrderProduct> orderProducts =
        createOrderProducts(products, request.getItems(), savedOrder);

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

    // 벌크 연산을 위한 OrderProduct 데이터 준비
    List<OrderProduct> newOrderProducts = new ArrayList<>();

    List<OrderLineItem> orderItems =
        originalOrderProducts.stream()
            .map(op -> new OrderLineItem(op.getCode(), op.getQuantity()))
            .collect(Collectors.toList());

    validateProductsForOrder(orderItems);

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

    // 메모리에서 OrderProduct 객체들 생성
    for (OrderProduct original : originalOrderProducts) {
      newOrderProducts.add(OrderProduct.of(
          original.getName(),
          original.getSubtitle(),
          original.getCode(),
          original.getListPrice(),
          original.getSellingPrice(),
          original.getQuantity(),
          savedReorder
      ));
    }

    // 벌크 insert 수행
    persistenceService.saveOrderProducts(newOrderProducts);

    return savedReorder;
  }

  /** 주문 처리 */
  @Transactional
  public OrderPayment addPayment(Long orderId, OrderPayment payment) {
    Order order = persistenceService.findOrder(orderId);
    OrderPayment savedPayment = persistenceService.savePayment(payment);

    BigDecimal totalPayments = calculateTotalPayments(order);

    if (isPaymentCompleted(totalPayments, order.getTotalSellingPrice())) {
      Profile profile = persistenceService.findUserProfile(order.getUser());
      updateOrderStatusAfterPayment(order, profile);
      persistenceService.save(order);
    }

    return savedPayment;
  }

  @Transactional
  public Order issueVouchers(Order order) {
    List<OrderProduct> orderProducts = persistenceService.findOrderProducts(order);

    for (OrderProduct orderProduct : orderProducts) {
      Product product = validateProductForVoucherIssue(orderProduct);

      List<Voucher> vouchers =
          persistenceService.findAvailableVouchers(
              orderProduct.getCode(), orderProduct.getQuantity());

      validateVouchersAvailability(orderProduct, vouchers, product);

      List<OrderProductVoucher> orderProductVouchers = new ArrayList<>();
      for (Voucher voucher : vouchers) {
        voucher.markAsSold();
        orderProductVouchers.add(
            OrderProductVoucher.builder()
                .orderProduct(orderProduct)
                .code(voucher.getCode())
                .remarks(voucher.getRemarks())
                .revoked(false)
                .build());
      }

      persistenceService.saveOrderProductVouchers(orderProductVouchers);

      product.updateStockQuantity(product.getStockQuantity() - orderProduct.getQuantity());
      persistenceService.updateProduct(product);
    }

    return order;
  }

  /** 주문 상태 관리 */
  @Transactional
  public void verifyOrder(Order order) {
    if (order.getStatus() != OrderStatus.PAYMENT_COMPLETED
        && order.getStatus() != OrderStatus.UNDER_REVIEW) {
      throw new IllegalStateException("결제 완료 또는 검토중 상태의 주문만 검증할 수 있습니다.");
    }

    order.updateStatus(OrderStatus.PAYMENT_VERIFIED);
    persistenceService.save(order);
  }

  @Transactional
  public void unverifyOrder(Order order) {
    if (order.getStatus() != OrderStatus.PAYMENT_VERIFIED) {
      throw new IllegalStateException("검증 완료 상태의 주문만 변경할 수 있습니다.");
    }

    order.updateStatus(OrderStatus.UNDER_REVIEW);
    persistenceService.save(order);
  }

  @Transactional
  public void softDeleteOrder(Long orderId) {
    Order order = persistenceService.findOrder(orderId);
    if (Boolean.TRUE.equals(order.getRemoved())) {
      throw new IllegalStateException("이미 삭제된 주문입니다.");
    }
    persistenceService.softDeleteOrder(order);
  }

  @Transactional
  public void softDeleteUserOrder(Integer userId, String orderNo) {
    Order order = persistenceService.findUserOrder(userId, orderNo);
    if (Boolean.TRUE.equals(order.getRemoved())) {
      throw new IllegalStateException("이미 삭제된 주문입니다.");
    }
    persistenceService.softDeleteUserOrder(order);
  }

  @Transactional
  public void hideOrder(Long orderId) {
    Order order = persistenceService.findOrder(orderId);
    if (OrderVisibility.HIDDEN.equals(order.getVisibility())) {
      throw new IllegalStateException("이미 숨김 처리된 주문입니다.");
    }
    persistenceService.hideOrder(order);
  }

  @Transactional
  public void hideUserOrder(Integer userId, String orderNo) {
    Order order = persistenceService.findUserOrder(userId, orderNo);
    if (OrderVisibility.HIDDEN.equals(order.getVisibility())) {
      throw new IllegalStateException("이미 숨김 처리된 주문입니다.");
    }
    persistenceService.hideUserOrder(order);
  }

  /** 환불 처리 */
  @Transactional
  public Order requestRefund(User user, Order order, String message) {
    validateRefundRequest(order);

    order.updateStatus(OrderStatus.REFUND_REQUESTED);
    order.updateMessage(message);
    persistenceService.save(order);

    Order refundOrder =
        Order.builder()
            .orderNo(generateOrderNumber())
            .fullname(order.getFullname())
            .userAgent(order.getUserAgent())
            .acceptLanguage(order.getAcceptLanguage())
            .ipAddress(order.getIpAddress())
            .totalListPrice(order.getTotalListPrice())
            .totalSellingPrice(order.getTotalSellingPrice())
            .currency(order.getCurrency())
            .parent(order)
            .user(user)
            .paymentMethod(order.getPaymentMethod())
            .status(OrderStatus.REFUND_PENDING)
            .visibility(OrderVisibility.VISIBLE)
            .transactionId("")
            .message(message)
            .suspicious(false)
            .removed(false)
            .build();

    Order savedRefundOrder = persistenceService.save(refundOrder);

    List<OrderProduct> originalOrderProducts = persistenceService.findOrderProducts(order);
    List<OrderProduct> refundOrderProducts =
        copyOrderProducts(originalOrderProducts, savedRefundOrder);
    persistenceService.saveOrderProducts(refundOrderProducts);

    revokeVouchers(order.getId());

    return savedRefundOrder;
  }

  @Transactional
  public Order completeRefund(Order refundOrder) {
    validateRefundCompletion(refundOrder);

    Order originalOrder = refundOrder.getParent();
    if (originalOrder == null) {
      throw new IllegalStateException("환불 처리할 원본 주문을 찾을 수 없습니다.");
    }

    originalOrder.updateStatus(OrderStatus.REFUNDED1);
    refundOrder.updateStatus(OrderStatus.REFUNDED2);

    persistenceService.save(originalOrder);
    persistenceService.save(refundOrder);

    return refundOrder;
  }

  /** 주문 생성 관련 헬퍼 메소드 */
  private String generateOrderNumber() {
    return UUID.randomUUID().toString().replace("-", "");
  }

  private List<Product> validateProductsForOrder(List<OrderLineItem> items) {
    List<Product> products = persistenceService.findProducts(items);

    Set<String> requestedCodes =
        items.stream().map(OrderLineItem::getCode).collect(Collectors.toSet());
    Set<String> foundCodes = products.stream().map(Product::getCode).collect(Collectors.toSet());

    if (!foundCodes.containsAll(requestedCodes)) {
      Set<String> notFoundCodes = new HashSet<>(requestedCodes);
      notFoundCodes.removeAll(foundCodes);
      throw new EntityNotFoundException("상품을 찾을 수 없습니다: " + String.join(", ", notFoundCodes));
    }

    Map<String, Integer> quantityByCode =
        items.stream()
            .collect(
                Collectors.groupingBy(
                    OrderLineItem::getCode, Collectors.summingInt(OrderLineItem::getQuantity)));

    List<String> errors = new ArrayList<>();
    for (Product product : products) {
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

  /** 주문 상품 관련 헬퍼 메소드 */
  private List<OrderProduct> createOrderProducts(
      List<Product> products, List<OrderLineItem> items, Order order) {
    Map<String, Product> productMap =
        products.stream().collect(Collectors.toMap(Product::getCode, Function.identity()));

    return items.stream()
        .map(
            item -> {
              Product product = productMap.get(item.getCode());
              return OrderProduct.of(
                  product.getName(),
                  product.getSubtitle(),
                  product.getCode(),
                  product.getListPrice(),
                  product.getSellingPrice(),
                  item.getQuantity(),
                  order);
            })
        .collect(Collectors.toList());
  }

  private List<OrderProduct> copyOrderProducts(
      List<OrderProduct> originalProducts, Order newOrder) {
    return originalProducts.stream()
        .map(
            op ->
                OrderProduct.of(
                    op.getName(),
                    op.getSubtitle(),
                    op.getCode(),
                    op.getListPrice(),
                    op.getSellingPrice(),
                    op.getQuantity(),
                    newOrder))
        .collect(Collectors.toList());
  }

  /** 가격 계산 관련 헬퍼 메소드 */
  private BigDecimal calculateTotalPrice(
      List<Product> products,
      List<OrderLineItem> items,
      Function<Product, BigDecimal> priceExtractor) {
    Map<String, Product> productMap =
        products.stream().collect(Collectors.toMap(Product::getCode, Function.identity()));

    return items.stream()
        .map(
            item -> {
              Product product = productMap.get(item.getCode());
              return priceExtractor.apply(product).multiply(BigDecimal.valueOf(item.getQuantity()));
            })
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  private BigDecimal calculateTotalPayments(Order order) {
    return persistenceService.findPaymentsByOrder(order).stream()
        .map(OrderPayment::getAmount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  private boolean isPaymentCompleted(BigDecimal totalPayments, BigDecimal orderAmount) {
    return totalPayments.compareTo(orderAmount) >= 0;
  }

  /** 바우처 관련 헬퍼 메소드 */
  private Product validateProductForVoucherIssue(OrderProduct orderProduct) {
    return persistenceService
        .findProducts(
            List.of(new OrderLineItem(orderProduct.getCode(), orderProduct.getQuantity())))
        .stream()
        .findFirst()
        .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다: " + orderProduct.getCode()));
  }

  private void validateVouchersAvailability(
      OrderProduct orderProduct, List<Voucher> vouchers, Product product) {
    if (vouchers.size() < orderProduct.getQuantity()) {
      throw new IllegalStateException(
          String.format(
              "상품 '%s'의 사용 가능한 상품권이 부족합니다. 필요: %d, 가용: %d",
              orderProduct.getName(), orderProduct.getQuantity(), vouchers.size()));
    }

    if (product.getStockQuantity() < vouchers.size()) {
      throw new IllegalStateException(
          String.format(
              "상품 '%s'의 재고와 상품권 수량이 불일치합니다. 재고: %d, 상품권: %d",
              product.getName(), product.getStockQuantity(), vouchers.size()));
    }
  }

  private void revokeVouchers(Long orderId) {
    List<OrderProductVoucher> vouchers = persistenceService.findOrderProductVouchers(orderId);

    vouchers.forEach(
        voucher -> {
          voucher.revoke();

          Voucher originalVoucher =
              persistenceService
                  .findVoucherByCode(voucher.getCode())
                  .orElseThrow(
                      () -> new EntityNotFoundException("상품권을 찾을 수 없습니다: " + voucher.getCode()));

          originalVoucher.markAsPurchased();
          persistenceService.updateVoucher(originalVoucher);
        });

    persistenceService.saveOrderProductVouchers(vouchers);
  }

  /** 상태 변경 관련 헬퍼 메소드 */
  private void validateRefundRequest(Order order) {
    if (order.getStatus() == OrderStatus.REFUND_REQUESTED
        || order.getStatus() == OrderStatus.REFUND_PENDING
        || order.getStatus() == OrderStatus.REFUNDED1
        || order.getStatus() == OrderStatus.REFUNDED2) {
      throw new IllegalStateException("이미 환불 처리된 주문입니다.");
    }
  }

  private void validateRefundCompletion(Order refundOrder) {
    if (refundOrder.getStatus() != OrderStatus.REFUND_PENDING) {
      throw new IllegalStateException("환불 처리 대기 상태의 주문이 아닙니다.");
    }

    Order originalOrder = refundOrder.getParent();
    if (originalOrder == null) {
      throw new IllegalStateException("환불 처리할 원본 주문을 찾을 수 없습니다.");
    }

    if (originalOrder.getStatus() != OrderStatus.REFUND_REQUESTED) {
      throw new IllegalStateException("환불 요청 상태의 주문이 아닙니다.");
    }
  }

  private void updateOrderStatusAfterPayment(Order order, Profile profile) {
    order.updateStatus(
        profile.isPhoneVerified() && profile.isDocumentVerified()
            ? OrderStatus.PAYMENT_VERIFIED
            : OrderStatus.UNDER_REVIEW);
  }
}
