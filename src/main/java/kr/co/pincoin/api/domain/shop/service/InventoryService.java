package kr.co.pincoin.api.domain.shop.service;

import java.util.Collection;
import java.util.List;
import kr.co.pincoin.api.app.admin.product.request.VoucherBulkCreateRequest;
import kr.co.pincoin.api.app.admin.product.request.VoucherBulkCreateRequest.VoucherCodeData;
import kr.co.pincoin.api.domain.shop.model.product.Product;
import kr.co.pincoin.api.domain.shop.model.product.Voucher;
import kr.co.pincoin.api.domain.shop.model.product.enums.VoucherStatus;
import kr.co.pincoin.api.global.exception.BusinessException;
import kr.co.pincoin.api.global.exception.ErrorCode;
import kr.co.pincoin.api.infra.shop.service.InventoryPersistenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InventoryService {
  private final InventoryPersistenceService inventoryPersistence;

  /**
   * 단일 상품권을 생성합니다.
   *
   * @param code 상품권 코드
   * @param productId 연결될 상품 ID
   * @param remarks 상품권 비고 (선택사항)
   * @return 생성된 상품권 엔티티
   * @throws BusinessException 상품을 찾을 수 없거나 중복된 상품권 코드가 존재하는 경우 발생
   */
  @Transactional
  public Voucher createVoucher(String code, Long productId, String remarks) {
    Product product =
        inventoryPersistence
            .findProductById(productId)
            .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

    if (inventoryPersistence.findVoucherByCode(code).isPresent()) {
      throw new BusinessException(ErrorCode.DUPLICATE_VOUCHER_CODE);
    }

    Voucher voucher = Voucher.of(code, product);

    if (remarks != null && !remarks.isEmpty()) {
      voucher.updateRemarks(remarks);
    }

    return inventoryPersistence.saveVoucher(voucher);
  }

  /**
   * 여러 상품권을 일괄 생성합니다.
   *
   * @param productId 연결될 상품 ID
   * @param voucherData 생성할 상품권 데이터 목록 (코드와 비고 포함)
   * @return 생성된 상품권 엔티티 목록
   * @throws BusinessException 상품을 찾을 수 없거나 중복된 상품권 코드가 존재하는 경우 발생
   */
  @Transactional
  public List<Voucher> createVouchersBulk(Long productId, List<VoucherCodeData> voucherData) {
    Product product =
        inventoryPersistence
            .findProductById(productId)
            .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

    List<String> codes =
        voucherData.stream().map(VoucherBulkCreateRequest.VoucherCodeData::getCode).toList();

    if (inventoryPersistence.hasAnyExistingVoucherCodes(codes)) {
      throw new BusinessException(ErrorCode.DUPLICATE_VOUCHER_CODES);
    }

    List<Voucher> vouchers =
        voucherData.stream()
            .map(
                data -> {
                  Voucher voucher = Voucher.of(data.getCode(), product);
                  if (data.getRemarks() != null && !data.getRemarks().isEmpty()) {
                    voucher.updateRemarks(data.getRemarks());
                  }
                  return voucher;
                })
            .toList();

    return inventoryPersistence.saveAllVouchers(vouchers);
  }

  /**
   * 단일 상품권의 상태를 업데이트합니다.
   *
   * @param voucherId 상품권 ID
   * @param status 변경할 상태 (PURCHASED, SOLD, REVOKED)
   * @return 업데이트된 상품권 엔티티
   * @throws BusinessException 상품권을 찾을 수 없거나 유효하지 않은 상태값인 경우 발생
   */
  @Transactional
  public Voucher updateVoucherStatus(Long voucherId, VoucherStatus status) {
    Voucher voucher =
        inventoryPersistence
            .findVoucherById(voucherId)
            .orElseThrow(() -> new BusinessException(ErrorCode.VOUCHER_NOT_FOUND));

    switch (status) {
      case PURCHASED -> voucher.markAsPurchased();
      case SOLD -> voucher.markAsSold();
      case REVOKED -> voucher.markAsRevoked();
      default -> throw new BusinessException(ErrorCode.INVALID_VOUCHER_STATUS);
    }

    return inventoryPersistence.saveVoucher(voucher);
  }

  /**
   * 여러 상품권의 상태를 일괄 업데이트합니다.
   *
   * @param voucherIds 상품권 ID 목록
   * @param status 변경할 상태 (PURCHASED, SOLD, REVOKED)
   * @return 업데이트된 상품권 엔티티 목록
   * @throws BusinessException 상품권을 찾을 수 없거나 유효하지 않은 상태값인 경우 발생
   */
  @Transactional
  public List<Voucher> updateVouchersStatus(Collection<Long> voucherIds, VoucherStatus status) {
    List<Voucher> vouchers = inventoryPersistence.findAllVouchersByIds(voucherIds);

    if (vouchers.size() != voucherIds.size()) {
      throw new BusinessException(ErrorCode.VOUCHER_NOT_FOUND);
    }

    vouchers.forEach(
        voucher -> {
          switch (status) {
            case PURCHASED -> voucher.markAsPurchased();
            case SOLD -> voucher.markAsSold();
            case REVOKED -> voucher.markAsRevoked();
            default -> throw new BusinessException(ErrorCode.INVALID_VOUCHER_STATUS);
          }
        });

    return inventoryPersistence.saveAllVouchers(vouchers);
  }
}
