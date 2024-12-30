package kr.co.pincoin.api.infra.shop.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import kr.co.pincoin.api.domain.shop.model.product.Product;
import kr.co.pincoin.api.domain.shop.model.product.Voucher;
import kr.co.pincoin.api.domain.shop.model.product.enums.ProductStatus;
import kr.co.pincoin.api.domain.shop.model.product.enums.VoucherStatus;
import kr.co.pincoin.api.domain.shop.repository.product.ProductRepository;
import kr.co.pincoin.api.domain.shop.repository.product.VoucherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class InventoryPersistenceService {

  private final VoucherRepository voucherRepository;

  private final ProductRepository productRepository;

  public Optional<Product> findProductById(Long id) {
    return productRepository.findById(id, ProductStatus.ENABLED, null);
  }

  public Optional<Voucher> findVoucherById(Long id) {
    return voucherRepository.findById(id);
  }

  public Optional<Voucher> findVoucherByCode(String code) {
    return voucherRepository.findByCode(code);
  }

  public boolean hasAnyExistingVoucherCodes(List<String> codes) {
    return !voucherRepository.findAllByCodeIn(codes).isEmpty();
  }

  public List<Voucher> findAllVouchersByIds(Collection<Long> ids) {
    return voucherRepository.findAllByIdIn(ids);
  }

  @Transactional
  public Voucher saveVoucher(Voucher voucher) {
    return voucherRepository.save(voucher);
  }

  @Transactional
  public List<Voucher> saveAllVouchers(List<Voucher> vouchers) {
    return voucherRepository.saveAll(vouchers).stream().toList();
  }

  /** 상품 코드와 수량으로 사용 가능한 바우처 목록 조회 */
  public List<Voucher> findAvailableVouchers(String productCode, int quantity) {
    return voucherRepository.findTopNByProductCodeAndStatusOrderByIdAsc(
        productCode, VoucherStatus.PURCHASED, quantity);
  }

  public Map<String, List<Voucher>> findAvailableVouchersByProductCodes(
      Collection<String> productCodes, Map<String, Integer> quantityByCode) {

    List<Voucher> allVouchers =
        voucherRepository.findAllByProductCodesAndStatus(productCodes, VoucherStatus.PURCHASED);

    return allVouchers.stream()
        .collect(
            Collectors.groupingBy(
                voucher -> voucher.getProduct().getCode(),
                Collectors.collectingAndThen(
                    Collectors.toList(),
                    vouchers -> {
                      if (vouchers.isEmpty()) {
                        return vouchers;
                      }
                      String productCode = vouchers.getFirst().getProduct().getCode();
                      int requiredQuantity = quantityByCode.get(productCode);
                      return vouchers.subList(0, Math.min(vouchers.size(), requiredQuantity));
                    })));
  }

  /** 바우처 업데이트 */
  @Transactional
  public void updateVoucher(Voucher voucher) {
    voucherRepository.save(voucher);
  }

  /** 바우처 목록 일괄 업데이트 */
  @Transactional
  public void updateVouchersBatch(List<Voucher> vouchers) {
    int batchSize = 100;
    for (int i = 0; i < vouchers.size(); i += batchSize) {
      List<Voucher> batch = vouchers.subList(i, Math.min(i + batchSize, vouchers.size()));
      voucherRepository.saveAll(batch);
    }
  }
}
