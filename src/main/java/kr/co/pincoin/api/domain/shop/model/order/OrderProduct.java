package kr.co.pincoin.api.domain.shop.model.order;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Getter
public class OrderProduct {
    private final Long id;
    private final String name;
    private final String subtitle;
    private final String code;
    private final BigDecimal listPrice;
    private final BigDecimal sellingPrice;
    private final Order order;
    private final LocalDateTime created;
    private final LocalDateTime modified;
    private Integer quantity;
    private Boolean isRemoved;

    @Builder
    private OrderProduct(Long id,
                         String name,
                         String subtitle,
                         String code,
                         BigDecimal listPrice,
                         BigDecimal sellingPrice,
                         Integer quantity,
                         Order order,
                         LocalDateTime created,
                         LocalDateTime modified,
                         Boolean isRemoved) {
        this.id = id;
        this.name = name;
        this.subtitle = subtitle;
        this.code = code;
        this.listPrice = listPrice;
        this.sellingPrice = sellingPrice;
        this.quantity = quantity;
        this.order = order;
        this.created = created;
        this.modified = modified;
        this.isRemoved = isRemoved;

        validatePrices();
        validateQuantity();
    }

    public static OrderProduct of(String name, String subtitle, String code,
                                  BigDecimal listPrice, BigDecimal sellingPrice,
                                  Integer quantity, Order order) {
        return OrderProduct.builder()
                .name(name)
                .subtitle(subtitle)
                .code(code)
                .listPrice(listPrice)
                .sellingPrice(sellingPrice)
                .quantity(quantity)
                .order(order)
                .build();
    }

    public void updateQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        this.quantity = quantity;
    }

    public void remove() {
        this.isRemoved = true;
    }

    public void restore() {
        this.isRemoved = false;
    }

    public BigDecimal getTotalListPrice() {
        return this.listPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public BigDecimal getTotalSellingPrice() {
        return this.sellingPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public BigDecimal getTotalDiscountAmount() {
        return getTotalListPrice().subtract(getTotalSellingPrice());
    }

    public double getDiscountRate() {
        if (this.listPrice.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        return getUnitDiscountAmount()
                .divide(this.listPrice, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }

    public BigDecimal getUnitDiscountAmount() {
        return this.listPrice.subtract(this.sellingPrice);
    }

    private void validatePrices() {
        if (listPrice == null || listPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("List price must be non-negative");
        }
        if (sellingPrice == null || sellingPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Selling price must be non-negative");
        }
        if (sellingPrice.compareTo(listPrice) > 0) {
            throw new IllegalArgumentException("Selling price cannot exceed list price");
        }
    }

    private void validateQuantity() {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
    }
}
