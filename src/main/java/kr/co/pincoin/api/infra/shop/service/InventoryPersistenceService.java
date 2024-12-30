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

    List<Voucher> allVouchers =
        voucherRepository.findAllByProductCodesAndStatus(productCodes, VoucherStatus.PURCHASED);

    return allVouchers.stream()
        // 상품권들을 상품 코드를 기준으로 그룹화
        .collect(
            Collectors.groupingBy(
                // 각 상품권의 상품 코드를 키로 사용
                voucher -> voucher.getProduct().getCode(),
                // 그룹화된 상품권 리스트를 추가 처리
                Collectors.collectingAndThen(
                    Collectors.toList(),
                    vouchers -> {
                      // 해당 상품에 대한 상품권이 없으면 빈 리스트 반환
                      if (vouchers.isEmpty()) {
                        return vouchers;
                      }
                      // 상품 코드 추출
                      String productCode = vouchers.getFirst().getProduct().getCode();
                      // 해당 상품에 대해 필요한 상품권 수량 확인
                      int requiredQuantity = quantityByCode.get(productCode);
                      // 필요한 수량만큼만 상품권 리스트 잘라서 반환
                      return vouchers.subList(0, Math.min(vouchers.size(), requiredQuantity));
                    })));
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
