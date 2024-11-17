package kr.co.pincoin.api.domain.shop.model.order;

import kr.co.pincoin.api.infra.shop.entity.order.OrderPaymentEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class OrderPayment {
    private final Long id;

    private final Integer account;

    private final BigDecimal amount;

    private final Order order;

    private final LocalDateTime created;

    private final LocalDateTime modified;

    private BigDecimal balance;

    private LocalDateTime received;

    private Boolean isRemoved;

    @Builder(access = AccessLevel.PRIVATE, builderMethodName = "instanceBuilder")
    private OrderPayment(Order order,
                         Integer account,
                         BigDecimal amount) {
        this.id = null;
        this.account = account;
        this.amount = amount;
        this.balance = BigDecimal.ZERO;
        this.received = null;
        this.order = order;
        this.created = LocalDateTime.now();
        this.modified = LocalDateTime.now();
        this.isRemoved = false;
    }

    @Builder(access = AccessLevel.PRIVATE, builderMethodName = "jpaBuilder")
    private OrderPayment(Long id,
                         Integer account,
                         BigDecimal amount,
                         BigDecimal balance,
                         LocalDateTime received,
                         Order order,
                         LocalDateTime created,
                         LocalDateTime modified,
                         Boolean isRemoved) {
        this.id = id;
        this.account = account;
        this.amount = amount;
        this.balance = balance;
        this.received = received;
        this.order = order;
        this.created = created;
        this.modified = modified;
        this.isRemoved = isRemoved;
    }

    public static OrderPayment of(Order order,
                                  Integer account,
                                  BigDecimal amount) {
        return OrderPayment.instanceBuilder()
                .order(order)
                .account(account)
                .amount(amount)
                .build();
    }

    public static OrderPayment from(OrderPaymentEntity entity) {
        return OrderPayment.jpaBuilder()
                .id(entity.getId())
                .account(entity.getAccount())
                .amount(entity.getAmount())
                .balance(entity.getBalance())
                .received(entity.getReceived())
                .order(Order.from(entity.getOrder()))
                .created(entity.getCreated())
                .modified(entity.getModified())
                .isRemoved(entity.getIsRemoved())
                .build();
    }

    public void receive(BigDecimal balance) {
        if (this.received != null) {
            throw new IllegalStateException("Payment already received");
        }

        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Balance cannot be negative");
        }

        this.received = LocalDateTime.now();
        this.balance = balance;
    }

    public void remove() {
        this.isRemoved = true;
    }

    public void restore() {
        this.isRemoved = false;
    }

    public boolean isReceived() {
        return this.received != null;
    }

    public boolean isPending() {
        return this.received == null;
    }

    public boolean isFullPayment() {
        return this.amount.compareTo(this.balance) == 0;
    }

    public BigDecimal getUnpaidAmount() {
        if (this.balance == null) {
            return this.amount;
        }
        return this.amount.subtract(this.balance);
    }

    public double getPaymentRate() {
        if (this.balance == null || this.amount.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        return this.balance.divide(this.amount, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }
}