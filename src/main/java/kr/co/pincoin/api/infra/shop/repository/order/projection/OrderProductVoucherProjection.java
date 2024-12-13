package kr.co.pincoin.api.infra.shop.repository.order.projection;

import lombok.Getter;

@Getter
public class OrderProductVoucherProjection {

    private final String productName;
    private final String productSubtitle;
    private final String voucherCode;
    private final String remarks;
    private final Boolean revoked;

    public OrderProductVoucherProjection(
        String productName,
        String productSubtitle,
        String voucherCode,
        String remarks,
        Boolean revoked) {
        this.productName = productName;
        this.productSubtitle = productSubtitle;
        this.voucherCode = voucherCode;
        this.remarks = remarks;
        this.revoked = revoked;
    }
}
