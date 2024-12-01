package kr.co.pincoin.api.app.admin.product.controller;

import java.util.Collection;
import java.util.List;
import kr.co.pincoin.api.app.admin.product.request.VoucherBulkCreateRequest;
import kr.co.pincoin.api.app.admin.product.request.VoucherCreateRequest;
import kr.co.pincoin.api.app.admin.product.response.VoucherResponse;
import kr.co.pincoin.api.app.admin.product.service.AdminVoucherService;
import kr.co.pincoin.api.domain.shop.model.product.Voucher;
import kr.co.pincoin.api.domain.shop.model.product.enums.VoucherStatus;
import kr.co.pincoin.api.global.response.success.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/vouchers")
@RequiredArgsConstructor
@Slf4j
public class AdminVoucherController {

  private final AdminVoucherService adminVoucherService;

  @PostMapping
  public ApiResponse<VoucherResponse> createVoucher(@RequestBody VoucherCreateRequest request) {
    Voucher voucher = adminVoucherService.createVoucher(request);
    return ApiResponse.of(
        VoucherResponse.from(voucher), HttpStatus.CREATED, "Voucher created successfully");
  }

  @PostMapping("/bulk")
  public ApiResponse<List<VoucherResponse>> createVouchersBulk(
      @RequestBody VoucherBulkCreateRequest request) {
    List<Voucher> vouchers = adminVoucherService.createVouchersBulk(request);
    List<VoucherResponse> responses = vouchers.stream().map(VoucherResponse::from).toList();
    return ApiResponse.of(responses, HttpStatus.CREATED, "Vouchers created successfully");
  }

  @PutMapping("/{voucherId}/status")
  public ApiResponse<VoucherResponse> updateVoucherStatus(
      @PathVariable Long voucherId, @RequestParam VoucherStatus status) {
    Voucher voucher = adminVoucherService.updateVoucherStatus(voucherId, status);
    return ApiResponse.of(VoucherResponse.from(voucher), "Voucher status updated successfully");
  }

  @PutMapping("/status/bulk")
  public ApiResponse<List<VoucherResponse>> updateVouchersStatus(
      @RequestParam Collection<Long> voucherIds, @RequestParam VoucherStatus status) {
    List<Voucher> vouchers = adminVoucherService.updateVouchersStatus(voucherIds, status);
    List<VoucherResponse> responses = vouchers.stream().map(VoucherResponse::from).toList();
    return ApiResponse.of(responses, "Vouchers status updated successfully");
  }
}
