package kr.co.pincoin.api.app.admin.product.controller;

import java.util.Collection;
import java.util.List;
import kr.co.pincoin.api.app.admin.product.request.VoucherBulkCreateRequest;
import kr.co.pincoin.api.app.admin.product.request.VoucherCreateRequest;
import kr.co.pincoin.api.app.admin.product.service.AdminVoucherService;
import kr.co.pincoin.api.domain.shop.model.product.Voucher;
import kr.co.pincoin.api.domain.shop.model.product.enums.VoucherStatus;
import kr.co.pincoin.api.global.response.model.VoucherResponse;
import kr.co.pincoin.api.global.response.success.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

  /**
   * 새로운 상품권을 생성합니다.
   *
   * @param request 상품권 생성에 필요한 정보를 담은 요청 객체
   * @return 생성된 상품권 정보를 포함한 ApiResponse
   */
  @PostMapping
  public ResponseEntity<ApiResponse<VoucherResponse>> createVoucher(
      @RequestBody VoucherCreateRequest request) {
    Voucher voucher = adminVoucherService.createVoucher(request);
    return ResponseEntity.ok(
        ApiResponse.of(
            VoucherResponse.from(voucher), HttpStatus.CREATED, "Voucher created successfully"));
  }

  /**
   * 여러 상품권을 일괄 생성합니다.
   *
   * @param request 일괄 생성할 상품권들의 정보를 담은 요청 객체
   * @return 생성된 상품권 목록을 포함한 ApiResponse
   */
  @PostMapping("/bulk")
  public ResponseEntity<ApiResponse<List<VoucherResponse>>> createVouchersBulk(
      @RequestBody VoucherBulkCreateRequest request) {
    List<Voucher> vouchers = adminVoucherService.createVouchersBulk(request);
    List<VoucherResponse> responses = vouchers.stream().map(VoucherResponse::from).toList();
    return ResponseEntity.ok(
        ApiResponse.of(responses, HttpStatus.CREATED, "Vouchers created successfully"));
  }

  /**
   * 상품권의 상태를 업데이트합니다.
   *
   * @param voucherId 상태를 변경할 상품권 ID
   * @param status 변경할 상품권 상태
   * @return 업데이트된 상품권 정보를 포함한 ApiResponse
   */
  @PutMapping("/{voucherId}/status")
  public ResponseEntity<ApiResponse<VoucherResponse>> updateVoucherStatus(
      @PathVariable Long voucherId, @RequestParam VoucherStatus status) {
    Voucher voucher = adminVoucherService.updateVoucherStatus(voucherId, status);
    return ResponseEntity.ok(
        ApiResponse.of(VoucherResponse.from(voucher), "Voucher status updated successfully"));
  }

  /**
   * 여러 상품권의 상태를 일괄 업데이트합니다.
   *
   * @param voucherIds 상태를 변경할 상품권 ID 목록
   * @param status 변경할 상품권 상태
   * @return 업데이트된 상품권 목록을 포함한 ApiResponse
   */
  @PutMapping("/status/bulk")
  public ResponseEntity<ApiResponse<List<VoucherResponse>>> updateVouchersStatus(
      @RequestParam Collection<Long> voucherIds, @RequestParam VoucherStatus status) {
    List<Voucher> vouchers = adminVoucherService.updateVouchersStatus(voucherIds, status);
    List<VoucherResponse> responses = vouchers.stream().map(VoucherResponse::from).toList();
    return ResponseEntity.ok(ApiResponse.of(responses, "Vouchers status updated successfully"));
  }
}
