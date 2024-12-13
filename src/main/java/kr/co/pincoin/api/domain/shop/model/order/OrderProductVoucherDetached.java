package kr.co.pincoin.api.domain.shop.model.order;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderProductVoucherDetached {

    private final Long id;
    private final String code;
    private final Long orderId;
    private final Long orderProductId;
    private final Long voucherId;
    private final LocalDateTime created;
    private final LocalDateTime modified;
    private final Boolean revoked;
    private final String remarks;
    private final Boolean isRemoved;

    @Builder
    private OrderProductVoucherDetached(
        Long id,
        String code,
        Long orderId,
        Long orderProductId,
        Long voucherId,
        LocalDateTime created,
        LocalDateTime modified,
        Boolean revoked,
        String remarks,
        Boolean isRemoved) {
        this.id = id;
        this.code = code;
        this.orderId = orderId;
        this.orderProductId = orderProductId;
        this.voucherId = voucherId;
        this.created = created;
        this.modified = modified;
        this.revoked = revoked;
        this.remarks = remarks;
        this.isRemoved = isRemoved;
    }
}