package kr.co.pincoin.api.domain.shop.converter;

import kr.co.pincoin.api.domain.shop.model.product.enums.VoucherStatus;

public class VoucherStatusConverter {
  public static VoucherStatus fromOrdinal(Number ordinal) {
    if (ordinal == null) {
      throw new IllegalArgumentException("VoucherStatus ordinal cannot be null");
    }

    return switch (ordinal.intValue()) {
      case 0 -> VoucherStatus.PURCHASED;
      case 1 -> VoucherStatus.SOLD;
      case 2 -> VoucherStatus.REVOKED;
      default -> throw new IllegalArgumentException("Unknown VoucherStatus ordinal: " + ordinal);
    };
  }

  public static VoucherStatus fromString(String status) {
    if (status == null) {
      throw new IllegalArgumentException("VoucherStatus string cannot be null");
    }

    return VoucherStatus.valueOf(status);
  }

  public static Integer toOrdinal(VoucherStatus status) {
    if (status == null) {
      throw new IllegalArgumentException("VoucherStatus cannot be null");
    }

    return status.getValue();
  }
}
