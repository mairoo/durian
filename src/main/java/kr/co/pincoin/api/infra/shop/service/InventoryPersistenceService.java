package kr.co.pincoin.api.infra.shop.service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import kr.co.pincoin.api.domain.shop.model.product.Product;
import kr.co.pincoin.api.domain.shop.model.product.Voucher;
import kr.co.pincoin.api.domain.shop.model.product.enums.ProductStatus;
import kr.co.pincoin.api.domain.shop.model.product.enums.VoucherStatus;
import kr.co.pincoin.api.domain.shop.repository.product.ProductRepository;
import kr.co.pincoin.api.domain.shop.repository.product.VoucherRepository;
import kr.co.pincoin.api.infra.shop.repository.product.projection.ProductVoucherCount;
import kr.co.pincoin.api.infra.shop.repository.product.projection.VoucherProjection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class InventoryPersistenceService {
  private final VoucherRepository voucherRepository;
  private final ProductRepository productRepository;

  /**
   * ID로 활성화된 상품을 조회합니다.
   *
   * @param id 상품 ID
   * @return 활성화된 상품 정보
   */
  public Optional<Product> findProductById(Long id) {
    return productRepository.findById(id, ProductStatus.ENABLED, null);
  }

  /**
   * 상품의 재고 수량을 감소시킵니다.
   *
   * @param productCode 상품 코드
   * @param quantity 감소시킬 수량
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public void decreaseStockQuantity(String productCode, int quantity) {
    productRepository.decreaseStockQuantity(productCode, quantity);
  }

  /**
   * 새로운 상품권을 저장합니다.
   *
   * @param voucher 저장할 상품권
   * @return 저장된 상품권
   */
  @Transactional
  public Voucher saveVoucher(Voucher voucher) {
    return voucherRepository.save(voucher);
  }

  /**
   * 상품권 목록을 일괄 저장합니다.
   *
   * @param vouchers 저장할 상품권 목록
   * @return 저장된 상품권 목록
   */
  @Transactional
  public List<Voucher> saveAllVouchers(List<Voucher> vouchers) {
    return voucherRepository.saveAll(vouchers).stream().toList();
  }

  /**
   * ID로 상품권을 조회합니다.
   *
   * @param id 상품권 ID
   * @return 상품권 정보
   */
  public Optional<Voucher> findVoucherById(Long id) {
    return voucherRepository.findById(id);
  }

  /**
   * 상품권 코드로 상품권을 조회합니다.
   *
   * @param code 상품권 코드
   * @return 상품권 정보
   */
  public Optional<Voucher> findVoucherByCode(String code) {
    return voucherRepository.findByCode(code);
  }

  /**
   * ID 목록으로 상품권 목록을 조회합니다.
   *
   * @param ids 상품권 ID 목록
   * @return 상품권 목록
   */
  public List<Voucher> findAllVouchersByIds(Collection<Long> ids) {
    return voucherRepository.findAllByIdIn(ids);
  }

  /**
   * 구매 가능한 상품권 목록을 조회합니다.
   *
   * @param productCode 상품 코드
   * @param quantity 조회할 수량
   * @return 구매 가능한 상품권 목록
   */
  public List<VoucherProjection> findAvailableVouchers(String productCode, int quantity) {
    return voucherRepository.findVouchersByProductStatus(
        productCode, VoucherStatus.PURCHASED, PageRequest.of(0, quantity));
  }

  /**
   * 상품 코드와 상태에 따른 상품권 수를 집계합니다.
   *
   * @param productCodes 상품 코드 목록
   * @param status 상품권 상태
   * @return 상품별 상품권 수 집계 결과
   */
  public List<ProductVoucherCount> countVouchersByProductCodesAndStatus(
      List<String> productCodes, VoucherStatus status) {
    return voucherRepository.countByProductCodesAndStatus(productCodes, status);
  }

  /**
   * 주어진 코드 목록 중 이미 존재하는 상품권 코드가 있는지 확인합니다.
   *
   * @param codes 확인할 상품권 코드 목록
   * @return 존재하는 상품권 코드가 있으면 true
   */
  public boolean hasAnyExistingVoucherCodes(List<String> codes) {
    return !voucherRepository.findAllByCodeIn(codes).isEmpty();
  }

  /**
   * 지정된 상품권들의 상태를 판매됨으로 업데이트합니다.
   *
   * @param voucherIds 상태를 변경할 상품권 ID 목록
   */
  @Transactional
  public void updateStatusToSold(List<Long> voucherIds) {
    voucherRepository.updateStatus(voucherIds, VoucherStatus.SOLD);
  }
}
