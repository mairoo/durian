package kr.co.pincoin.api.app.admin.product.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kr.co.pincoin.api.app.admin.product.request.VoucherBulkCreateRequest;
import kr.co.pincoin.api.app.admin.product.request.VoucherCreateRequest;
import kr.co.pincoin.api.domain.shop.model.product.Product;
import kr.co.pincoin.api.domain.shop.model.product.Voucher;
import kr.co.pincoin.api.domain.shop.model.product.enums.VoucherStatus;
import kr.co.pincoin.api.domain.shop.repository.product.ProductRepository;
import kr.co.pincoin.api.domain.shop.repository.product.VoucherRepository;
import kr.co.pincoin.api.global.exception.BusinessException;
import kr.co.pincoin.api.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminVoucherService {
  private final VoucherRepository voucherRepository;

  private final ProductRepository productRepository;

  /** 단일 상품권 등록 */
  @Transactional
  public Voucher createVoucher(VoucherCreateRequest request) {
    // 상품 조회
    Product product =
        productRepository
            .findById(request.getProductId())
            .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

    // 상품권 코드 중복 검사
    if (voucherRepository.existsByCode(request.getCode())) {
      throw new BusinessException(ErrorCode.DUPLICATE_VOUCHER_CODE);
    }

    // 상품권 생성
    Voucher voucher = Voucher.of(request.getCode(), product);

    if (request.getRemarks() != null && !request.getRemarks().isEmpty()) {
      voucher.updateRemarks(request.getRemarks());
    }

    return voucherRepository.save(voucher);
  }

  /** 상품권 일괄 등록 */
  @Transactional
  public List<Voucher> createVouchersBulk(VoucherBulkCreateRequest request) {
    // 상품 조회
    Product product =
        productRepository
            .findById(request.getProductId())
            .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

    // 코드 중복 검사 (일괄)
    List<String> codes = new ArrayList<>();
    for (VoucherBulkCreateRequest.VoucherCodeData data : request.getVoucherData()) {
      codes.add(data.getCode());
    }

    List<Voucher> existingVouchers = voucherRepository.findAllByCodeIn(codes);
    if (!existingVouchers.isEmpty()) {
      throw new BusinessException(ErrorCode.DUPLICATE_VOUCHER_CODES);
    }

    // 상품권 생성 (일괄)
    List<Voucher> vouchers = new ArrayList<>();
    for (VoucherBulkCreateRequest.VoucherCodeData data : request.getVoucherData()) {
      Voucher voucher = Voucher.of(data.getCode(), product);
      if (data.getRemarks() != null && !data.getRemarks().isEmpty()) {
        voucher.updateRemarks(data.getRemarks());
      }
      vouchers.add(voucher);
    }

    return voucherRepository.saveAll(vouchers).stream().toList();
  }

  /** 단일 상품권 상태 변경 */
  @Transactional
  public Voucher updateVoucherStatus(Long voucherId, VoucherStatus status) {
    Voucher voucher =
        voucherRepository
            .findById(voucherId)
            .orElseThrow(() -> new BusinessException(ErrorCode.VOUCHER_NOT_FOUND));

    switch (status) {
      case PURCHASED -> voucher.markAsPurchased();
      case SOLD -> voucher.markAsSold();
      case REVOKED -> voucher.markAsRevoked();
      default -> throw new BusinessException(ErrorCode.INVALID_VOUCHER_STATUS);
    }

    return voucherRepository.save(voucher);
  }

  /** 상품권 일괄 상태 변경 */
  @Transactional
  public List<Voucher> updateVouchersStatus(Collection<Long> voucherIds, VoucherStatus status) {
    List<Voucher> vouchers = voucherRepository.findAllByIdIn(voucherIds);

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

    return voucherRepository.saveAll(vouchers).stream().toList();
  }
}
