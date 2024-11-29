package kr.co.pincoin.api.domain.shop.model.order;

import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderCurrency;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderPaymentMethod;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderStatus;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderVisibility;
import kr.co.pincoin.api.infra.shop.entity.order.OrderEntity;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Getter
public class Order {
    private final Long id;

    private final String orderNo;

    private final String fullname;

    private final String userAgent;

    private final String acceptLanguage;

    private final String ipAddress;

    private final BigDecimal totalListPrice;

    private final BigDecimal totalSellingPrice;

    private final OrderCurrency currency;

    private final Order parent;

    private final User user;

    private final LocalDateTime created;

    private final LocalDateTime modified;

    private OrderPaymentMethod paymentMethod;

    private OrderStatus status;

    private OrderVisibility visibility;

    private String transactionId;

    private String message;

    private Boolean suspicious;

    private Boolean removed;

    @Builder
    private Order(Long id,
                  String orderNo,
                  String fullname,
                  String userAgent,
                  String acceptLanguage,
                  String ipAddress,
                  BigDecimal totalListPrice,
                  BigDecimal totalSellingPrice,
                  OrderCurrency currency,
                  Order parent,
                  User user,
                  LocalDateTime created,
                  LocalDateTime modified,
                  OrderPaymentMethod paymentMethod,
                  OrderStatus status,
                  OrderVisibility visibility,
                  String transactionId,
                  String message,
                  Boolean suspicious,
                  Boolean removed) {
        this.id = id;
        this.orderNo = orderNo;
        this.fullname = fullname;
        this.userAgent = userAgent;
        this.acceptLanguage = acceptLanguage;
        this.ipAddress = ipAddress;
        this.totalListPrice = totalListPrice;
        this.totalSellingPrice = totalSellingPrice;
        this.currency = currency;
        this.parent = parent;
        this.user = user;
        this.created = created;
        this.modified = modified;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.visibility = visibility;
        this.transactionId = transactionId;
        this.message = message;
        this.suspicious = suspicious;
        this.removed = removed;
    }

    public static Order of(User user,
                           String orderNo,
                           String fullname,
                           String userAgent,
                           String acceptLanguage,
                           String ipAddress,
                           BigDecimal totalListPrice,
                           BigDecimal totalSellingPrice,
                           OrderCurrency currency,
                           OrderPaymentMethod paymentMethod) {
        return Order.builder()
                // 1. 불변 필드 (user 관련)
                .user(user)
                .orderNo(orderNo)
                .fullname(fullname)
                .userAgent(userAgent)
                .acceptLanguage(acceptLanguage)
                .ipAddress(ipAddress)
                .totalListPrice(totalListPrice)
                .totalSellingPrice(totalSellingPrice)
                .currency(currency)

                // 2. 주문 상태
                .paymentMethod(paymentMethod)
                .build();
    }

    public OrderEntity toEntity() {
        return OrderEntity.builder()
                // 1. 불변 필드
                .id(this.getId())
                .orderNo(this.getOrderNo())
                .fullname(this.getFullname())
                .userAgent(this.getUserAgent())
                .acceptLanguage(this.getAcceptLanguage())
                .ipAddress(this.getIpAddress())
                .totalListPrice(this.getTotalListPrice())
                .totalSellingPrice(this.getTotalSellingPrice())
                .currency(this.getCurrency())
                .parent(this.getParent() != null ? this.getParent().toEntity() : null)
                .user(this.getUser().toEntity())

                // 2. 주문 상태
                .paymentMethod(this.getPaymentMethod())
                .status(this.getStatus())
                .visibility(this.getVisibility())
                .transactionId(this.getTransactionId())

                // 3. 기타 정보
                .message(this.getMessage())
                .suspicious(this.getSuspicious())
                .build();
    }

    // 1. 상태/속성 변경 메소드들 (updateXXX)
    public void updateStatus(OrderStatus status) {
        this.status = status;
    }

    public void updateVisibility(OrderVisibility visibility) {
        this.visibility = visibility;
    }

    public void updatePaymentMethod(OrderPaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void updateTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public void updateMessage(String message) {
        this.message = message;
    }

    // 2. 특수 상태 변경 메소드들 (markAs, remove 등)
    public void markAsSuspicious(String message) {
        this.suspicious = true;
        this.message = message;
    }

    public void softDelete() {
        this.removed = true;
    }

    public void restore() {
        this.removed = false;
    }

    // 3. 상태 확인 메소드들 (isXXX, hasXXX)
    public boolean isRemoved() {
        return this.removed != null;
    }

    public boolean isSuspicious() {
        return this.suspicious != null && this.suspicious;
    }

    // 4. 계산 메소드들 (getXXX, calculateXXX)
    public BigDecimal getDiscountAmount() {
        return this.totalListPrice.subtract(this.totalSellingPrice);
    }

    public double getDiscountRate() {
        if (this.totalListPrice.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        return this.getDiscountAmount()
                .divide(this.totalListPrice, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }
}
