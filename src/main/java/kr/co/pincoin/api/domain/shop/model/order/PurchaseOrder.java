package kr.co.pincoin.api.domain.shop.model.order;

import kr.co.pincoin.api.infra.shop.entity.order.PurchaseOrderEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class PurchaseOrder {
    private final Long id;
    private final BigDecimal amount;
    private final LocalDateTime created;
    private final LocalDateTime modified;
    private String title;
    private String content;
    private Boolean paid;
    private String bankAccount;
    private Boolean isRemoved;

    @Builder(access = AccessLevel.PRIVATE, builderMethodName = "instanceBuilder")
    private PurchaseOrder(String title, String content, String bankAccount, BigDecimal amount) {
        this.id = null;
        this.title = title;
        this.content = content;
        this.paid = false;
        this.bankAccount = bankAccount;
        this.amount = amount;
        this.created = LocalDateTime.now();
        this.modified = LocalDateTime.now();
        this.isRemoved = false;

        validatePurchaseOrder();
    }

    @Builder(access = AccessLevel.PRIVATE, builderMethodName = "jpaBuilder")
    private PurchaseOrder(Long id, String title, String content,
                          Boolean paid, String bankAccount, BigDecimal amount,
                          LocalDateTime created, LocalDateTime modified,
                          Boolean isRemoved) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.paid = paid;
        this.bankAccount = bankAccount;
        this.amount = amount;
        this.created = created;
        this.modified = modified;
        this.isRemoved = isRemoved;

        validatePurchaseOrder();
    }

    public static PurchaseOrder of(String title, String content,
                                   String bankAccount, BigDecimal amount) {
        return PurchaseOrder.instanceBuilder()
                .title(title)
                .content(content)
                .bankAccount(bankAccount)
                .amount(amount)
                .build();
    }

    public static PurchaseOrder from(PurchaseOrderEntity entity) {
        return PurchaseOrder.jpaBuilder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .paid(entity.getPaid())
                .bankAccount(entity.getBankAccount())
                .amount(entity.getAmount())
                .created(entity.getCreated())
                .modified(entity.getModified())
                .isRemoved(entity.getIsRemoved())
                .build();
    }

    public void updateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        this.title = title;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void updateBankAccount(String bankAccount) {
        if (this.paid) {
            throw new IllegalStateException("Cannot update bank account for paid purchase order");
        }
        this.bankAccount = bankAccount;
    }

    public void markAsPaid() {
        if (this.paid) {
            throw new IllegalStateException("Purchase order is already paid");
        }
        this.paid = true;
    }

    public void markAsUnpaid() {
        if (!this.paid) {
            throw new IllegalStateException("Purchase order is already unpaid");
        }
        this.paid = false;
    }

    public void remove() {
        if (this.paid) {
            throw new IllegalStateException("Cannot remove paid purchase order");
        }
        this.isRemoved = true;
    }

    public void restore() {
        this.isRemoved = false;
    }

    public boolean isPaid() {
        return this.paid;
    }

    public boolean isPending() {
        return !this.paid;
    }

    public boolean isValidBankAccount() {
        return this.bankAccount != null && !this.bankAccount.trim().isEmpty();
    }

    private void validatePurchaseOrder() {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }
}
