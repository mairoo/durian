package kr.co.pincoin.api.infra.shop.repository.product.projection;

import java.time.LocalDateTime;
import kr.co.pincoin.api.domain.shop.model.product.enums.VoucherStatus;

public record VoucherProjection(
    Long id,
    String code,
    String remarks,
    LocalDateTime created,
    LocalDateTime modified,
    VoucherStatus status,
    Boolean isRemoved) {}
