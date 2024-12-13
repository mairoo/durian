package kr.co.pincoin.api.domain.shop.model.product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import kr.co.pincoin.api.global.exception.BusinessException;
import kr.co.pincoin.api.global.exception.ErrorCode;
import kr.co.pincoin.api.infra.shop.entity.product.PurchaseOrderEntity;
import lombok.Builder;
import lombok.Getter;

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

  @Builder
  private PurchaseOrder(
      Long id,
      String title,
      String content,
      Boolean paid,
      String bankAccount,
      BigDecimal amount,
      LocalDateTime created,
      LocalDateTime modified,
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

  public PurchaseOrderEntity toEntity() {
    return PurchaseOrderEntity.builder()
        .id(this.getId())
        .title(this.getTitle())
        .content(this.getContent())
        .paid(this.getPaid())
        .bankAccount(this.getBankAccount())
        .amount(this.getAmount())
        .build();
  }

  public static PurchaseOrder of(
      String title, String content, String bankAccount, BigDecimal amount) {
    return PurchaseOrder.builder()
        .title(title)
        .content(content)
        .bankAccount(bankAccount)
        .amount(amount)
        .build();
  }

  public void updateTitle(String title) {
    if (title == null || title.trim().isEmpty()) {
      throw new BusinessException(ErrorCode.CANNOT_UPDATE_PAID_PURCHASE_ORDER);
    }
    this.title = title;
  }

  public void updateContent(String content) {
    this.content = content;
  }

  public void updateBankAccount(String bankAccount) {
    if (this.paid) {
      throw new BusinessException(ErrorCode.PURCHASE_ORDER_ALREADY_PAID);
    }
    this.bankAccount = bankAccount;
  }

  public void markAsPaid() {
    if (this.paid) {
      throw new BusinessException(ErrorCode.PURCHASE_ORDER_ALREADY_PAID);
    }
    this.paid = true;
  }

  public void markAsUnpaid() {
    if (!this.paid) {
      throw new BusinessException(ErrorCode.PURCHASE_ORDER_ALREADY_UNPAID);
    }
    this.paid = false;
  }

  public void softDelete() {
    if (this.paid) {
      throw new BusinessException(ErrorCode.CANNOT_REMOVE_PAID_PURCHASE_ORDER);
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
      throw new BusinessException(ErrorCode.PURCHASE_ORDER_TITLE_REQUIRED);
    }
    if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new BusinessException(ErrorCode.PURCHASE_ORDER_AMOUNT_MUST_BE_POSITIVE);
    }
  }
}
