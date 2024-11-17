package kr.co.pincoin.api.domain.shop.model.order;

import kr.co.pincoin.api.domain.auth.model.user.User;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderStatus;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderVisibility;
import kr.co.pincoin.api.infra.shop.entity.order.OrderEntity;
import lombok.AccessLevel;
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

    private final Integer paymentMethod;

    private final String transactionId;

    private final BigDecimal totalListPrice;

    private final BigDecimal totalSellingPrice;

    private final String currency;

    private final Order parent;

    private final User user;

    private final LocalDateTime created;

    private final LocalDateTime modified;

    private Integer status;

    private Integer visible;

    private String message;

    private Boolean suspicious;

    private Boolean removed;

    @Builder(access = AccessLevel.PRIVATE, builderMethodName = "instanceBuilder")
    private Order(User user,
                  String orderNo,
                  String fullname,
                  String userAgent,
                  String acceptLanguage,
                  String ipAddress,
                  Integer paymentMethod,
                  BigDecimal totalListPrice,
                  BigDecimal totalSellingPrice,
                  String currency) {
        this.id = null;
        this.orderNo = orderNo;
        this.fullname = fullname;
        this.userAgent = userAgent;
        this.acceptLanguage = acceptLanguage;
        this.ipAddress = ipAddress;
        this.paymentMethod = paymentMethod;
        this.transactionId = null;
        this.status = OrderStatus.PENDING.getValue();
        this.visible = OrderVisibility.VISIBLE.getValue();
        this.totalListPrice = totalListPrice;
        this.totalSellingPrice = totalSellingPrice;
        this.currency = currency;
        this.message = null;
        this.suspicious = false;
        this.parent = null;
        this.user = user;
        this.created = LocalDateTime.now();
        this.modified = LocalDateTime.now();
        this.removed = null;
    }

    @Builder(access = AccessLevel.PRIVATE, builderMethodName = "jpaBuilder")
    private Order(Long id,
                  String orderNo,
                  String fullname,
                  String userAgent,
                  String acceptLanguage,
                  String ipAddress,
                  Integer paymentMethod,
                  String transactionId,
                  Integer status,
                  Integer visible,
                  BigDecimal totalListPrice,
                  BigDecimal totalSellingPrice,
                  String currency,
                  String message,
                  Boolean suspicious,
                  Order parent,
                  User user,
                  LocalDateTime created,
                  LocalDateTime modified,
                  Boolean removed) {
        this.id = id;
        this.orderNo = orderNo;
        this.fullname = fullname;
        this.userAgent = userAgent;
        this.acceptLanguage = acceptLanguage;
        this.ipAddress = ipAddress;
        this.paymentMethod = paymentMethod;
        this.transactionId = transactionId;
        this.status = status;
        this.visible = visible;
        this.totalListPrice = totalListPrice;
        this.totalSellingPrice = totalSellingPrice;
        this.currency = currency;
        this.message = message;
        this.suspicious = suspicious;
        this.parent = parent;
        this.user = user;
        this.created = created;
        this.modified = modified;
        this.removed = removed;
    }

    public static Order of(User user,
                           String orderNo,
                           String fullname,
                           String userAgent,
                           String acceptLanguage,
                           String ipAddress,
                           Integer paymentMethod,
                           BigDecimal totalListPrice,
                           BigDecimal totalSellingPrice,
                           String currency) {
        return Order.instanceBuilder()
                .user(user)
                .orderNo(orderNo)
                .fullname(fullname)
                .userAgent(userAgent)
                .acceptLanguage(acceptLanguage)
                .ipAddress(ipAddress)
                .paymentMethod(paymentMethod)
                .totalListPrice(totalListPrice)
                .totalSellingPrice(totalSellingPrice)
                .currency(currency)
                .build();
    }

    public static Order from(OrderEntity entity) {
        return Order.jpaBuilder()
                .id(entity.getId())
                .orderNo(entity.getOrderNo())
                .fullname(entity.getFullname())
                .userAgent(entity.getUserAgent())
                .acceptLanguage(entity.getAcceptLanguage())
                .ipAddress(entity.getIpAddress())
                .paymentMethod(entity.getPaymentMethod())
                .transactionId(entity.getTransactionId())
                .status(entity.getStatus())
                .visible(entity.getVisible())
                .totalListPrice(entity.getTotalListPrice())
                .totalSellingPrice(entity.getTotalSellingPrice())
                .currency(entity.getCurrency())
                .message(entity.getMessage())
                .suspicious(entity.getSuspicious())
                .parent(entity.getParent() != null ? Order.from(entity.getParent()) : null)
                .user(User.from(entity.getUser()))
                .created(entity.getCreated())
                .modified(entity.getModified())
                .removed(entity.getIsRemoved())
                .build();
    }

    public OrderEntity toEntity() {
        return OrderEntity.builder()
                .id(this.getId())
                .orderNo(this.getOrderNo())
                .fullname(this.getFullname())
                .userAgent(this.getUserAgent())
                .acceptLanguage(this.getAcceptLanguage())
                .ipAddress(this.getIpAddress())
                .paymentMethod(this.getPaymentMethod())
                .transactionId(this.getTransactionId())
                .status(this.getStatus())
                .visible(this.getVisible())
                .totalListPrice(this.getTotalListPrice())
                .totalSellingPrice(this.getTotalSellingPrice())
                .currency(this.getCurrency())
                .message(this.getMessage())
                .suspicious(this.getSuspicious())
                .parent(this.getParent() != null ? this.getParent().toEntity() : null)
                .user(this.getUser().toEntity())
                .build();
    }

    public void markAsSuspicious(String message) {
        this.suspicious = true;
        this.message = message;
    }

    public void updateStatus(OrderStatus status) {
        this.status = status.getValue();
    }

    public void hide() {
        this.visible = OrderVisibility.HIDDEN.getValue();
    }

    public void show() {
        this.visible = OrderVisibility.VISIBLE.getValue();
    }

    public void remove() {
        this.removed = true;
    }

    public void restore() {
        this.removed = false;
    }

    public boolean isPending() {
        return OrderStatus.PENDING.getValue().equals(this.status);
    }

    public boolean isCompleted() {
        return OrderStatus.COMPLETED.getValue().equals(this.status);
    }

    public boolean isCancelled() {
        return OrderStatus.CANCELLED.getValue().equals(this.status);
    }

    public boolean isVisible() {
        return OrderVisibility.VISIBLE.getValue().equals(this.visible);
    }

    public boolean isRemoved() {
        return this.removed != null;
    }

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
