package kr.co.pincoin.api.domain.shop.service;

import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import kr.co.pincoin.api.domain.auth.model.profile.Profile;
import kr.co.pincoin.api.domain.shop.event.order.OrderPaymentCompletedEvent;
import kr.co.pincoin.api.domain.shop.model.order.Order;
import kr.co.pincoin.api.domain.shop.model.order.OrderPayment;
import kr.co.pincoin.api.domain.shop.model.order.OrderPaymentDetached;
import kr.co.pincoin.api.domain.shop.model.order.OrderProduct;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderStatus;
import kr.co.pincoin.api.infra.auth.mapper.profile.ProfileMapper;
import kr.co.pincoin.api.infra.auth.service.UserProfilePersistenceService;
import kr.co.pincoin.api.infra.shop.mapper.order.OrderMapper;
import kr.co.pincoin.api.infra.shop.mapper.order.OrderProductMapper;
import kr.co.pincoin.api.infra.shop.repository.order.projection.OrderProductProjection;
import kr.co.pincoin.api.infra.shop.service.OrderPaymentPersistenceService;
import kr.co.pincoin.api.infra.shop.service.OrderPersistenceService;
import kr.co.pincoin.api.infra.shop.service.OrderProductPersistenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderPaymentProcessingService {

  // 주문 한도 금액
  private static final BigDecimal AMOUNT_100K = new BigDecimal("100000");

  private static final BigDecimal AMOUNT_200K = new BigDecimal("200000");

  private static final BigDecimal AMOUNT_300K = new BigDecimal("300000");

  private static final BigDecimal AMOUNT_500K = new BigDecimal("500000");

  // 검증 필요 상품권
  private static final List<String> UNSAFE_VOUCHERS =
      List.of("문화상품권", "컬쳐랜드상품권", "도서문화상품권", "구글기프트카드");

  // 주문이력 유효성 검사 기간 상수
  private static final long FIRST_PURCHASE_MIN_DAYS = 14;

  private static final long LAST_PURCHASE_MAX_DAYS = 30;

  private final UserProfilePersistenceService userProfilePersistenceService;

  private final OrderPersistenceService orderPersistenceService;

  private final OrderPaymentPersistenceService orderPaymentPersistenceService;

  private final OrderProductPersistenceService orderProductPersistenceService;

  private final OrderMapper orderMapper;

  private final OrderProductMapper orderProductMapper;

  private final ProfileMapper profileMapper;

  private final ApplicationEventPublisher eventPublisher;

  /**
   * 주문에 새로운 결제를 추가하고, 결제 완료 여부에 따라 주문 상태를 업데이트한다. (총 쿼리 4회) 결제가 완료되면 OrderPaymentCompletedEvent를
   * 발행한다.
   *
   * @param orderId 결제를 추가할 주문 ID
   * @param payment 추가할 결제 정보
   * @return 저장된 결제 정보
   */
  @Transactional
  public OrderPayment addPayment(Long orderId, OrderPayment payment) {
    // 1. 주문 정보, 사용자, 프로필 모두 함께 조회
    List<OrderProductProjection> projections =
        orderProductPersistenceService.findAllWithOrderUserProfileByOrderId(orderId);

    if (projections.isEmpty()) {
      throw new EntityNotFoundException("주문 없음: " + orderId);
    }

    OrderProductProjection firstProjection = projections.getFirst();

    Order order = orderMapper.toModel(firstProjection.getOrder());

    Profile profile = profileMapper.toModel(firstProjection.getProfile());

    List<OrderProduct> orderProducts =
        projections.stream()
            .map(projection -> orderProductMapper.toModel(projection.getOrderProduct()))
            .collect(Collectors.toList());

    // 2. 새로운 OrderPayment 객체 생성 (불변 객체)
    OrderPayment newPayment =
        OrderPayment.builder()
            .id(payment.getId())
            .account(payment.getAccount())
            .amount(payment.getAmount())
            .balance(payment.getBalance())
            .received(payment.getReceived())
            .order(order) // 생성 시점에 order 연관
            .created(payment.getCreated())
            .modified(payment.getModified())
            .isRemoved(payment.getIsRemoved())
            .build();

    // 3. 결제 정보 저장
    OrderPayment savedPayment = orderPaymentPersistenceService.savePayment(newPayment);

    // 4. 총 결제 금액 계산
    BigDecimal totalPayments = orderPaymentPersistenceService.getTotalAmountByOrder(order);

    // 5. 결제 완료 여부 확인 후 처리
    if (isPaymentCompleted(totalPayments, order.getTotalSellingPrice())) {

      // 5.1. 주문 상태 결정
      OrderStatus newStatus = determineOrderStatus(order, profile, orderProducts, totalPayments);
      order.updateStatus(newStatus);

      // 5.2. 결제 완료 이벤트 발행
      eventPublisher.publishEvent(new OrderPaymentCompletedEvent(order, savedPayment));

      // 5.3. 변경된 주문 상태 저장
      orderPersistenceService.save(order);
    }

    return savedPayment;
  }

  /**
   * 특정 주문에 대한 모든 결제 내역을 조회한다.
   *
   * @param order 조회할 주문 엔티티
   * @return 해당 주문의 모든 결제 내역 목록
   */
  public List<OrderPayment> getPayments(Order order) {
    return orderPaymentPersistenceService.findPaymentsByOrder(order);
  }

  /**
   * 주문 ID를 이용하여 분리된(detached) 결제 내역을 조회한다. 분리된 결제 내역은 엔티티가 아닌 DTO 형태로 반환된다.
   *
   * @param orderId 조회할 주문의 ID
   * @return 해당 주문의 분리된 결제 내역 목록
   */
  public List<OrderPaymentDetached> getPayments(Long orderId) {
    return orderPaymentPersistenceService.findOrderPayments(orderId);
  }

  /**
   * 주문에 대한 총 결제 금액을 계산하여 반환한다. 취소된 결제는 제외하고 계산된다.
   *
   * @param order 조회할 주문 엔티티
   * @return 총 결제 금액
   */
  public BigDecimal getTotalPaymentAmount(Order order) {
    return orderPaymentPersistenceService.getTotalAmountByOrder(order);
  }

  /**
   * 결제 정보의 유효성을 검증한다. 결제 금액이 0보다 커야 하며, 필요한 경우 결제 방법별 추가 검증을 수행한다.
   *
   * @param payment 검증할 결제 정보
   * @throws IllegalArgumentException 결제 금액이 0 이하인 경우
   */
  public void validatePayment(OrderPayment payment) {
    if (payment.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("결제 금액은 0보다 커야 합니다.");
    }
  }

  /**
   * 결제 완료 여부를 확인한다. 총 결제 금액이 주문 금액보다 크거나 같으면 결제가 완료된 것으로 간주한다.
   *
   * @param totalPayments 총 결제 금액
   * @param orderAmount 주문 금액
   * @return 결제 완료 여부
   */
  private boolean isPaymentCompleted(BigDecimal totalPayments, BigDecimal orderAmount) {
    return totalPayments.compareTo(orderAmount) >= 0;
  }

  /**
   * 주문의 모든 결제가 완료되었는지 확인한다. 총 결제 금액이 판매 가격보다 크거나 같으면 완료된 것으로 간주한다.
   *
   * @param order 확인할 주문 정보
   * @return 결제 완료 여부
   */
  public boolean isOrderFullyPaid(Order order) {
    BigDecimal totalPayments = getTotalPaymentAmount(order);
    return totalPayments.compareTo(order.getTotalSellingPrice()) >= 0;
  }

  /**
   * 결제 취소 가능 여부를 확인한다. 이미 삭제된 결제이거나 환불된 주문의 결제는 취소할 수 없다.
   *
   * @param payment 취소 가능 여부를 확인할 결제 정보
   * @return 결제 취소 가능 여부
   */
  public boolean isCancellable(OrderPayment payment) {
    return !Boolean.TRUE.equals(payment.getIsRemoved())
        && payment.getOrder().getStatus() != OrderStatus.REFUNDED1
        && payment.getOrder().getStatus() != OrderStatus.REFUNDED2;
  }

  /**
   * 프로필 인증 상태와 주문 이력에 따라 주문 상태를 결정한다. 주문 금액, 인증 상태, 상품 종류, 주문 이력 등을 고려하여 적절한 상태를 반환한다.
   *
   * @param order 상태를 결정할 주문
   * @param profile 사용자 프로필 정보
   * @return 결정된 주문 상태
   */
  private OrderStatus determineOrderStatus(
      Order order, Profile profile, List<OrderProduct> orderProducts, BigDecimal totalPayments) {

    if (totalPayments.compareTo(order.getTotalSellingPrice()) < 0) {
      return OrderStatus.PAYMENT_PENDING;
    }

    boolean hasSafeVouchers = hasSafeVouchers(orderProducts); // 이미 조회된 orderProducts 사용
    boolean isPhoneVerified = profile.isPhoneVerified();
    boolean isDocumentVerified = profile.isDocumentVerified();
    BigDecimal totalListPrice = order.getTotalListPrice();
    int totalOrderCount = profile.getTotalOrderCount();

    // 입금액 부족 입금확인중 상태 유지
    if (totalPayments.compareTo(order.getTotalSellingPrice()) < 0) {
      return OrderStatus.PAYMENT_PENDING;
    }

    // 기본 인증 상태 체크 - 휴대폰과 서류 인증이 모두 없는 경우
    if (!isPhoneVerified && !isDocumentVerified) {
      if (totalOrderCount == 0) {
        return OrderStatus.UNDER_REVIEW; // 첫 주문이면서 인증이 없으면 심사 필요
      }
    }

    // 신규 고객 처리 로직
    if (totalOrderCount == 0) {
      if (isPhoneVerified) {
        // 20만원 미만은 휴대폰 인증만으로 가능
        if (totalListPrice.compareTo(AMOUNT_200K) < 0) {
          return OrderStatus.PAYMENT_VERIFIED;
        }
        // 30만원 미만이면서 안전한 상품권인 경우
        if (totalListPrice.compareTo(AMOUNT_300K) < 0 && hasSafeVouchers) {
          return OrderStatus.PAYMENT_VERIFIED;
        }
      }
    }

    // 기존 고객 처리 로직
    else if (totalOrderCount > 0) {
      if (totalOrderCount > 5 // 구매 횟수 5회 초과
          && isOrderHistoryValid(order, profile) // 첫 구매 14일 경과 && 마지막 구매 30일 이내
          && order.getTotalSellingPrice().compareTo(profile.getMaxPrice()) <= 0 // 고액구매 아닌 경우
      ) {
        // 모든 인증이 완료된 경우
        if (isPhoneVerified && isDocumentVerified) {
          return OrderStatus.PAYMENT_VERIFIED;
        }
        // 하나의 인증이라도 있고 안전한 상품권인 경우
        if ((isPhoneVerified || isDocumentVerified) && hasSafeVouchers) {
          return OrderStatus.PAYMENT_VERIFIED;
        }
      }

      // 금액별 상세 처리 로직
      // 10만원 이하, 서류 인증 완료
      if (totalListPrice.compareTo(AMOUNT_100K) <= 0 && isDocumentVerified) {
        return OrderStatus.PAYMENT_VERIFIED;
      }

      // 20만원 미만, 휴대폰 인증만
      if (totalListPrice.compareTo(AMOUNT_200K) < 0 && isPhoneVerified) {
        return OrderStatus.PAYMENT_VERIFIED;
      }

      // 20만원 이하, 휴대폰 + 서류 인증
      if (totalListPrice.compareTo(AMOUNT_200K) <= 0 && isPhoneVerified && isDocumentVerified) {
        return OrderStatus.PAYMENT_VERIFIED;
      }

      // 30만원 미만, 휴대폰 인증, 안전 상품권
      if (totalListPrice.compareTo(AMOUNT_300K) < 0 && isPhoneVerified && hasSafeVouchers) {
        return OrderStatus.PAYMENT_VERIFIED;
      }

      // 50만원 이하, 모든 인증 완료, 안전 상품권
      if (totalListPrice.compareTo(AMOUNT_500K) <= 0
          && isPhoneVerified
          && isDocumentVerified
          && hasSafeVouchers) {
        return OrderStatus.PAYMENT_VERIFIED;
      }
    }

    // 위의 모든 조건을 만족하지 않으면 입금완료 상태로 설정
    return OrderStatus.PAYMENT_COMPLETED;
  }

  /**
   * 주문에 안전한 상품권이 포함되어 있는지 확인한다. 문화상품권, 해피머니, 도서문화상품권을 제외한 상품권이 있으면 안전한 것으로 간주한다.
   *
   * @param orderProducts 확인할 주문
   * @return 안전한 상품권 포함 여부
   */
  private boolean hasSafeVouchers(List<OrderProduct> orderProducts) {
    return orderProducts.stream().anyMatch(product -> !UNSAFE_VOUCHERS.contains(product.getName()));
  }

  /**
   * 주문 이력이 유효한지 확인한다. 첫 구매로부터 14일이 경과하고, 마지막 구매가 30일 이내여야 유효하다.
   *
   * @param order 현재 주문
   * @param profile 사용자 프로필
   * @return 주문 이력 유효 여부
   */
  private boolean isOrderHistoryValid(Order order, Profile profile) {
    LocalDateTime now = LocalDateTime.now();

    // 첫 구매일이나 마지막 구매일이 없으면 유효하지 않음
    if (profile.getFirstPurchased() == null || profile.getLastPurchased() == null) {
      return false;
    }

    // 첫 구매 14일 경과, 마지막 구매 30일 이내 확인
    return ChronoUnit.DAYS.between(profile.getFirstPurchased(), now) > FIRST_PURCHASE_MIN_DAYS
        && ChronoUnit.DAYS.between(profile.getLastPurchased(), now) < LAST_PURCHASE_MAX_DAYS;
  }
}
