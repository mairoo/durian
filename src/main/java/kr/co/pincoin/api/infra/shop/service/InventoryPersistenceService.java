package kr.co.pincoin.api.infra.shop.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import kr.co.pincoin.api.domain.shop.model.product.Product;
import kr.co.pincoin.api.domain.shop.model.product.Voucher;
import kr.co.pincoin.api.domain.shop.model.product.enums.ProductStatus;
import kr.co.pincoin.api.domain.shop.model.product.enums.VoucherStatus;
import kr.co.pincoin.api.domain.shop.repository.product.ProductRepository;
import kr.co.pincoin.api.domain.shop.repository.product.VoucherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class InventoryPersistenceService {

  private final VoucherRepository voucherRepository;

  private final ProductRepository productRepository;

  /**
   * 상품권을 저장합니다.
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
   * ID로 활성화된 상품을 조회합니다.
   *
   * @param id 상품 ID
   * @return 상품 정보
   */
  public Optional<Product> findProductById(Long id) {
    return productRepository.findById(id, ProductStatus.ENABLED, null);
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
   * 상품 코드 목록에 해당하는 사용 가능한 상품권을 조회합니다.
   *
   * @param productCodes 상품 코드 목록
   * @param quantityByCode 상품 코드별 필요한 상품권 수량
   * @return 상품 코드별 사용 가능한 상품권 목록
   */
  public Map<String, List<Voucher>> findAvailableVouchersByProductCodes(
      Collection<String> productCodes, Map<String, Integer> quantityByCode) {

    Map<String, List<Voucher>> result = new HashMap<>();

    // - 단일 쿼리로 모든 상품권 가져오기
    // - 상품별로 별도 쿼리 실행하고 필요한 수량만 가져오기 (현재)
    //
    // 대부분의 구매자는 상품권종을 5가지 이하로 구매하므로 별도 쿼리 실행 방식 도입
    for (String productCode : productCodes) {
      int quantity = quantityByCode.get(productCode);
      PageRequest pageRequest = PageRequest.of(0, quantity);

      List<Voucher> vouchers =
          voucherRepository.findAllByProductCodeAndStatus(
              productCode, VoucherStatus.PURCHASED, pageRequest);

      result.put(productCode, vouchers);
    }

    return result;
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
   * 상품권 정보를 업데이트합니다.
   *
   * @param voucher 업데이트할 상품권
   */
  @Transactional
  public void updateVoucher(Voucher voucher) {
    voucherRepository.save(voucher);
  }

  /**
   * 상품권 목록을 배치 크기(100개)로 나누어 일괄 업데이트합니다.
   *
   * @param vouchers 업데이트할 상품권 목록
   */
  @Transactional
  public void updateVouchersBatch(List<Voucher> vouchers) {
    int batchSize = 100;
    for (int i = 0; i < vouchers.size(); i += batchSize) {
      List<Voucher> batch = vouchers.subList(i, Math.min(i + batchSize, vouchers.size()));
      voucherRepository.saveAll(batch);
    }
  }
}
