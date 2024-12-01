package kr.co.pincoin.api.app.admin.product.service;

import java.util.Collection;
import java.util.List;
import kr.co.pincoin.api.app.admin.product.request.VoucherBulkCreateRequest;
import kr.co.pincoin.api.app.admin.product.request.VoucherCreateRequest;
import kr.co.pincoin.api.domain.shop.model.product.Voucher;
import kr.co.pincoin.api.domain.shop.model.product.enums.VoucherStatus;
import kr.co.pincoin.api.domain.shop.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminVoucherService {

  private final InventoryService inventoryService;

  @Transactional
  public Voucher createVoucher(VoucherCreateRequest request) {
    return inventoryService.createVoucher(
        request.getCode(),
        request.getProductId(),
        request.getRemarks()
    );
  }

  @Transactional
  public List<Voucher> createVouchersBulk(VoucherBulkCreateRequest request) {
    return inventoryService.createVouchersBulk(
        request.getProductId(),
        request.getVoucherData()
    );
  }

  @Transactional
  public Voucher updateVoucherStatus(Long voucherId, VoucherStatus status) {
    return inventoryService.updateVoucherStatus(voucherId, status);
  }

  @Transactional
  public List<Voucher> updateVouchersStatus(Collection<Long> voucherIds, VoucherStatus status) {
    return inventoryService.updateVouchersStatus(voucherIds, status);
  }
}
