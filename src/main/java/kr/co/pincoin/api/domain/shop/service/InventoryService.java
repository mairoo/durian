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

    @Transactional
    public Voucher createVoucher(String code, Long productId, String remarks) {
        Product product = inventoryPersistence.findProductById(productId)
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

    @Transactional
    public List<Voucher> createVouchersBulk(Long productId, List<VoucherCodeData> voucherData) {
        Product product = inventoryPersistence.findProductById(productId)
            .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        List<String> codes = voucherData.stream()
            .map(VoucherBulkCreateRequest.VoucherCodeData::getCode)
            .toList();

        if (inventoryPersistence.hasAnyExistingVoucherCodes(codes)) {
            throw new BusinessException(ErrorCode.DUPLICATE_VOUCHER_CODES);
        }

        List<Voucher> vouchers = voucherData.stream()
            .map(data -> {
                Voucher voucher = Voucher.of(data.getCode(), product);
                if (data.getRemarks() != null && !data.getRemarks().isEmpty()) {
                    voucher.updateRemarks(data.getRemarks());
                }
                return voucher;
            })
            .toList();

        return inventoryPersistence.saveAllVouchers(vouchers);
    }

    @Transactional
    public Voucher updateVoucherStatus(Long voucherId, VoucherStatus status) {
        Voucher voucher = inventoryPersistence.findVoucherById(voucherId)
            .orElseThrow(() -> new BusinessException(ErrorCode.VOUCHER_NOT_FOUND));

        switch (status) {
            case PURCHASED -> voucher.markAsPurchased();
            case SOLD -> voucher.markAsSold();
            case REVOKED -> voucher.markAsRevoked();
            default -> throw new BusinessException(ErrorCode.INVALID_VOUCHER_STATUS);
        }

        return inventoryPersistence.saveVoucher(voucher);
    }

    @Transactional
    public List<Voucher> updateVouchersStatus(Collection<Long> voucherIds, VoucherStatus status) {
        List<Voucher> vouchers = inventoryPersistence.findAllVouchersByIds(voucherIds);

        if (vouchers.size() != voucherIds.size()) {
            throw new BusinessException(ErrorCode.VOUCHER_NOT_FOUND);
        }

        vouchers.forEach(voucher -> {
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