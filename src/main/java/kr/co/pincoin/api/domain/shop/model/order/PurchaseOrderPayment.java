package kr.co.pincoin.api.domain.shop.model.order;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class PurchaseOrderPayment {
    private final Long id;
    private final Integer account;
    private final BigDecimal amount;
    private final PurchaseOrder order;
    private final LocalDateTime created;
    private final LocalDateTime modified;
    private Boolean isRemoved;

    @Builder
    private PurchaseOrderPayment(Long id, Integer account, BigDecimal amount,
                                 PurchaseOrder order, LocalDateTime created,
                                 LocalDateTime modified, Boolean isRemoved) {
        this.id = id;
        this.account = account;
        this.amount = amount;
        this.order = order;
        this.created = created;
        this.modified = modified;
        this.isRemoved = isRemoved;

        validatePayment();
    }

    public static PurchaseOrderPayment of(Integer account, BigDecimal amount,
                                          PurchaseOrder order) {
        return PurchaseOrderPayment.builder()
                .account(account)
                .amount(amount)
                .order(order)
                .build();
    }

    public void remove() {
        if (this.order.isPaid()) {
            throw new IllegalStateException("Cannot remove payment for paid purchase order");
        }
        this.isRemoved = true;
    }

    public void restore() {
        this.isRemoved = false;
    }

    public boolean isFullPayment() {
        return this.amount.compareTo(this.order.getAmount()) == 0;
    }

    public boolean isPartialPayment() {
        return this.amount.compareTo(this.order.getAmount()) < 0;
    }

    public boolean isOverPayment() {
        return this.amount.compareTo(this.order.getAmount()) > 0;
    }

    public BigDecimal getRemainingAmount() {
        return this.order.getAmount().subtract(this.amount);
    }

    public double getPaymentRate() {
        if (this.order.getAmount().compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        return this.amount
                .divide(this.order.getAmount(), 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }

    private void validatePayment() {
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (order == null) {
            throw new IllegalArgumentException("Purchase order cannot be null");
        }
        if (order.isPaid()) {
            throw new IllegalStateException("Cannot create payment for already paid purchase order");
        }
    }

    public enum Account {
        BANK(1),
        ESCROW(2);

        private final Integer value;

        Account(Integer value) {
            this.value = value;
        }

        public static Account fromValue(Integer value) {
            for (Account account : Account.values()) {
                if (account.value.equals(value)) {
                    return account;
                }
            }
            throw new IllegalArgumentException("Invalid account value: " + value);
        }

        public Integer getValue() {
            return value;
        }
    }
}