package kr.co.pincoin.api.infra.shop.repository.order.projection;

public record OrderProductVoucherProjection(
    String productName,
    String productSubtitle,
    String voucherCode,
    String remarks,
    Boolean revoked) {}
