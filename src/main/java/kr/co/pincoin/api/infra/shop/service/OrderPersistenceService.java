package kr.co.pincoin.api.infra.shop.service;

import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import kr.co.pincoin.api.app.member.order.request.CartItem;
import kr.co.pincoin.api.domain.auth.model.profile.Profile;
import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.domain.auth.repository.profile.ProfileRepository;
import kr.co.pincoin.api.domain.auth.repository.user.UserRepository;
import kr.co.pincoin.api.domain.shop.model.order.Order;
import kr.co.pincoin.api.domain.shop.model.order.OrderPayment;
import kr.co.pincoin.api.domain.shop.model.order.OrderProduct;
import kr.co.pincoin.api.domain.shop.model.order.OrderProductDetached;
import kr.co.pincoin.api.domain.shop.model.order.OrderProductVoucher;
import kr.co.pincoin.api.domain.shop.model.order.condition.OrderProductSearchCondition;
import kr.co.pincoin.api.domain.shop.model.order.condition.OrderSearchCondition;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderVisibility;
import kr.co.pincoin.api.domain.shop.model.product.Product;
import kr.co.pincoin.api.domain.shop.model.product.ProductDetached;
import kr.co.pincoin.api.domain.shop.model.product.Voucher;
import kr.co.pincoin.api.domain.shop.model.product.enums.ProductStatus;
import kr.co.pincoin.api.domain.shop.model.product.enums.ProductStock;
import kr.co.pincoin.api.domain.shop.model.product.enums.VoucherStatus;
import kr.co.pincoin.api.domain.shop.repository.order.OrderPaymentRepository;
import kr.co.pincoin.api.domain.shop.repository.order.OrderProductRepository;
import kr.co.pincoin.api.domain.shop.repository.order.OrderProductVoucherRepository;
import kr.co.pincoin.api.domain.shop.repository.order.OrderRepository;
import kr.co.pincoin.api.domain.shop.repository.product.ProductRepository;
import kr.co.pincoin.api.domain.shop.repository.product.VoucherRepository;
import kr.co.pincoin.api.infra.shop.repository.order.projection.OrderProductVoucherProjection;
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
  private final OrderProductRepository orderProductRepository;
  private final OrderPaymentRepository orderPaymentRepository;
  private final OrderProductVoucherRepository orderProductVoucherRepository;
  private final ProductRepository productRepository;
  private final VoucherRepository voucherRepository;
  private final ProfileRepository profileRepository;
  private final UserRepository userRepository;

  //==========================================
  // Order 관련 CRUD
  //==========================================

  /**
   * 주문 ID로 주문 엔티티 조회
   */
  public Order findOrder(Long orderId) {
    return orderRepository
        .findById(orderId)
        .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다."));
  }

  /**
   * 주문 ID로 사용자 정보가 포함된 주문 조회
   */
  public Order findOrderWithUser(Long orderId) {
    return orderRepository
        .findByIdWithUser(orderId)
        .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다."));
  }

  /**
   * 사용자 ID와 주문번호로 주문 조회
   */
  public Order findUserOrder(Integer userId, String orderNo) {
    return orderRepository
        .findByOrderNoAndUserId(orderNo, userId)
        .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다."));
  }

  /**
   * 주문 검색 조건으로 주문 목록 페이징 조회
   */
  public Page<Order> searchOrders(OrderSearchCondition condition, Pageable pageable) {
    OrderSearchCondition searchCondition =
        Optional.ofNullable(condition).orElseGet(OrderSearchCondition::empty);

    Pageable pageRequest = Optional.ofNullable(pageable).orElse(Pageable.unpaged());

    return orderRepository.searchOrders(searchCondition, pageRequest);
  }

  /**
   * 사용자 주문 검색 조건으로 주문 목록 페이징 조회
   */
  public Page<Order> searchUserOrders(OrderSearchCondition condition, Pageable pageable) {
    return searchOrders(
        Optional.ofNullable(condition).orElseGet(OrderSearchCondition::empty),
        pageable);
  }

  /**
   * 주문 저장
   */
  @Transactional
  public Order save(Order order) {
    return orderRepository.save(order);
  }

  /**
   * 주문 저장 및 즉시 반영
   */
  @Transactional
  public Order saveAndFlush(Order order) {
    return orderRepository.saveAndFlush(order);
  }

  /**
   * 원본 주문과 환불 주문 일괄 저장
   */
  @Transactional
  public void saveOrders(Order originalOrder, Order refundOrder) {
    orderRepository.saveAll(List.of(originalOrder, refundOrder));
  }

  /**
   * 주문 소프트 삭제
   */
  @Transactional
  public void softDeleteOrder(Order order) {
    order.softDelete();
    save(order);
  }

  /**
   * 주문 숨김 처리
   */
  @Transactional
  public void hideOrder(Order order) {
    order.updateVisibility(OrderVisibility.HIDDEN);
    save(order);
  }

  /**
   * 사용자 주문 숨김 처리
   */
  @Transactional
  public void hideUserOrder(Order order) {
    order.updateVisibility(OrderVisibility.HIDDEN);
    save(order);
  }

  //==========================================
  // OrderProduct 관련 CRUD
  //==========================================

  /**
   * 주문 ID로 주문상품 목록 조회
   */
  public List<OrderProduct> findOrderProductsByOrderId(Long orderId) {
    return orderProductRepository.findAll(OrderProductSearchCondition.ofOrderId(orderId));
  }

  /**
   * 사용자 ID와 주문번호로 주문상품 목록 조회
   */
  public List<OrderProduct> findOrderProductsByUserIdAndOrderNo(Integer userId, String orderNo) {
    return orderProductRepository.findAll(
        OrderProductSearchCondition.ofOrderNo(orderNo).withUserId(userId));
  }

  /**
   * 사용자 ID와 주문번호로 분리된 주문상품 목록 조회
   */
  public List<OrderProductDetached> findOrderProductsDetachedByUserIdAndOrderNo(
      Integer userId, String orderNo) {
    return orderProductRepository.findAllDetached(
        OrderProductSearchCondition.ofOrderNo(orderNo).withUserId(userId));
  }

  /**
   * 주문에 속한 주문상품 목록 조회
   */
  public List<OrderProduct> findOrderProductsWithOrder(Order order) {
    return orderProductRepository.findAllWithOrder(order);
  }

  /**
   * 원본 주문상품 목록 조회 (사용자 정보 포함)
   */
  public List<OrderProduct> findOriginalOrderProducts(String orderNo, Integer userId) {
    return orderProductRepository.findAllWithOrderAndUser(orderNo, userId);
  }

  /**
   * 주문상품 목록 저장
   */
  @Transactional
  public void saveOrderProducts(List<OrderProduct> orderProducts) {
    int batchSize = 100;
    for (int i = 0; i < orderProducts.size(); i += batchSize) {
      List<OrderProduct> batch =
          orderProducts.subList(i, Math.min(i + batchSize, orderProducts.size()));
      orderProductRepository.saveAll(batch);
    }
  }

  /**
   * 주문상품 목록 일괄 저장 (배치)
   */
  @Transactional
  public void saveOrderProductsBatch(List<OrderProduct> orderProducts) {
    int batchSize = 100;
    for (int i = 0; i < orderProducts.size(); i += batchSize) {
      List<OrderProduct> batch =
          orderProducts.subList(i, Math.min(i + batchSize, orderProducts.size()));
      orderProductRepository.saveAll(batch);
    }
  }

  //==========================================
  // OrderPayment 관련 CRUD
  //==========================================

  /**
   * 주문의 결제내역 조회 (삭제되지 않은 결제만)
   */
  public List<OrderPayment> findPaymentsByOrder(Order order) {
    return orderPaymentRepository.findByOrderAndIsRemovedFalse(order);
  }

  /**
   * 주문 ID로 결제내역 조회
   */
  public List<OrderPayment> findOrderPayments(Long orderId) {
    return orderPaymentRepository.findByOrderId(orderId);
  }

  /**
   * 주문의 총 결제금액 조회
   */
  public BigDecimal getTotalAmountByOrder(Order order) {
    return orderPaymentRepository.getTotalAmountByOrder(order);
  }

  /**
   * 결제내역 저장
   */
  @Transactional
  public OrderPayment savePayment(OrderPayment payment) {
    return orderPaymentRepository.save(payment);
  }

  //==========================================
  // OrderProductVoucher 관련 CRUD
  //==========================================

  /**
   * 주문 ID로 주문상품 바우처 목록 조회
   */
  public List<OrderProductVoucherProjection> findOrderProductVouchers(Long orderId) {
    return orderProductVoucherRepository.findAllByOrderProductOrderId(orderId);
  }

  /**
   * 주문상품 바우처 목록 저장
   */
  @Transactional
  public void saveOrderProductVouchers(List<OrderProductVoucher> vouchers) {
    orderProductVoucherRepository.saveAll(vouchers);
  }

  /**
   * 주문상품 바우처 목록 일괄 저장 (배치)
   */
  @Transactional
  public void saveOrderProductVouchersBatch(List<OrderProductVoucher> vouchers) {
    int batchSize = 100;
    for (int i = 0; i < vouchers.size(); i += batchSize) {
      List<OrderProductVoucher> batch =
          vouchers.subList(i, Math.min(i + batchSize, vouchers.size()));
      orderProductVoucherRepository.saveAll(batch);
    }
  }

  //==========================================
  // Product 관련 CRUD
  //==========================================

  /**
   * 장바구니 아이템으로 상품 목록 조회
   */
  public List<Product> findProductsByCartItems(List<CartItem> items) {
    return productRepository.findAllByCodeIn(
        items.stream().map(CartItem::getCode).distinct().toList());
  }

  /**
   * 상품 코드 목록으로 상품 Map 조회
   */
  public Map<String, Product> findProductsByCodeIn(List<String> codes) {
    return productRepository.findAllByCodeIn(codes).stream()
        .collect(Collectors.toMap(Product::getCode, Function.identity()));
  }

  /**
   * 상품 코드 목록으로 분리된 상품 Map 조회 (활성화되고 재고있는 상품만)
   */
  public Map<String, ProductDetached> findProductsDetachedByCodeIn(List<String> codes) {
    return productRepository.findAllDetachedByCodeIn(codes, ProductStatus.ENABLED,
            ProductStock.IN_STOCK)
        .stream()
        .collect(Collectors.toMap(ProductDetached::getCode, Function.identity()));
  }

  /**
   * 상품 업데이트
   */
  @Transactional
  public void updateProduct(Product product) {
    productRepository.save(product);
  }

  /**
   * 상품 목록 일괄 업데이트
   */
  @Transactional
  public void updateProductsBatch(List<Product> products) {
    int batchSize = 100;
    for (int i = 0; i < products.size(); i += batchSize) {
      List<Product> batch = products.subList(i, Math.min(i + batchSize, products.size()));
      productRepository.saveAll(batch);
    }
  }

  //==========================================
  // Voucher 관련 CRUD
  //==========================================

  /**
   * 상품 코드와 수량으로 사용 가능한 바우처 목록 조회
   */
  public List<Voucher> findAvailableVouchers(String productCode, int quantity) {
    return voucherRepository.findTopNByProductCodeAndStatusOrderByIdAsc(
        productCode, VoucherStatus.PURCHASED, quantity);
  }

  /**
   * 바우처 코드로 바우처 조회
   */
  public Optional<Voucher> findVoucherByCode(String code) {
    return voucherRepository.findByCode(code);
  }

  /**
   * 바우처 업데이트
   */
  @Transactional
  public void updateVoucher(Voucher voucher) {
    voucherRepository.save(voucher);
  }

  /**
   * 바우처 목록 일괄 업데이트
   */
  @Transactional
  public void updateVouchersBatch(List<Voucher> vouchers) {
    int batchSize = 100;
    for (int i = 0; i < vouchers.size(); i += batchSize) {
      List<Voucher> batch = vouchers.subList(i, Math.min(i + batchSize, vouchers.size()));
      voucherRepository.saveAll(batch);
    }
  }

  //==========================================
  // User/Profile 관련 CRUD
  //==========================================

  /**
   * 사용자 ID로 프로필 조회
   */
  public Profile findProfileByOrderUserId(Integer userId) {
    return profileRepository
        .findByUserId(userId)
        .orElseThrow(() -> new EntityNotFoundException("사용자 프로필을 찾을 수 없습니다."));
  }

  /**
   * 사용자 ID로 사용자 조회
   */
  public User findUser(Integer userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
  }

  /**
   * 사용자로 프로필 조회
   */
  public Profile findUserProfile(User user) {
    return profileRepository
        .findByUserWithUser(user)
        .orElseThrow(() -> new EntityNotFoundException("사용자 프로필을 찾을 수 없습니다."));
  }
}