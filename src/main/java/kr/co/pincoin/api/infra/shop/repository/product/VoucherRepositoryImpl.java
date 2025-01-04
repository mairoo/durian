package kr.co.pincoin.api.infra.shop.repository.product;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import kr.co.pincoin.api.domain.shop.model.product.Voucher;
import kr.co.pincoin.api.domain.shop.model.product.enums.VoucherStatus;
import kr.co.pincoin.api.domain.shop.repository.product.VoucherRepository;
import kr.co.pincoin.api.global.exception.BusinessException;
import kr.co.pincoin.api.global.exception.ErrorCode;
import kr.co.pincoin.api.infra.shop.mapper.product.VoucherMapper;
import kr.co.pincoin.api.infra.shop.repository.product.projection.ProductVoucherCount;
import kr.co.pincoin.api.infra.shop.repository.product.projection.VoucherProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class VoucherRepositoryImpl implements VoucherRepository {
  private final VoucherJpaRepository jpaRepository;
  private final VoucherQueryRepository queryRepository;
  private final VoucherMapper mapper;

  /**
   * 상품권을 생성하거나 수정합니다.
   *
   * @param voucher 저장할 상품권
   * @return 저장된 상품권
   */
  @Override
  public Voucher save(Voucher voucher) {
    return mapper.toModel(jpaRepository.save(mapper.toEntity(voucher)));
  }

  /**
   * 여러 상품권을 일괄 저장합니다.
   *
   * @param vouchers 저장할 상품권 목록
   * @return 저장된 상품권 목록
   */
  @Override
  public List<Voucher> saveAll(Collection<Voucher> vouchers) {
    return mapper.toModelList(
        jpaRepository.saveAll(mapper.toEntityList(vouchers.stream().toList())));
  }

  /**
   * ID로 상품권을 조회합니다.
   *
   * @param id 상품권 ID
   * @return 조회된 상품권 (없을 경우 Optional.empty)
   */
  @Override
  public Optional<Voucher> findById(Long id) {
    return jpaRepository.findById(id).map(mapper::toModel);
  }

  /**
   * 코드로 상품권을 조회합니다.
   *
   * @param code 상품권 코드
   * @return 조회된 상품권 (없을 경우 Optional.empty)
   */
  @Override
  public Optional<Voucher> findByCode(String code) {
    return jpaRepository.findByCode(code).map(mapper::toModel);
  }

  /**
   * ID 목록으로 여러 상품권을 조회합니다.
   *
   * @param ids 상품권 ID 목록
   * @return 조회된 상품권 목록
   */
  @Override
  public List<Voucher> findAllByIdIn(Collection<Long> ids) {
    return jpaRepository.findAllByIdIn(ids).stream()
        .map(mapper::toModel)
        .collect(Collectors.toList());
  }

  /**
   * 코드 목록으로 여러 상품권을 조회합니다.
   *
   * @param codes 상품권 코드 목록
   * @return 조회된 상품권 목록
   */
  @Override
  public List<Voucher> findAllByCodeIn(Collection<String> codes) {
    return jpaRepository.findAllByCodeIn(codes).stream()
        .map(mapper::toModel)
        .collect(Collectors.toList());
  }

  /**
   * 상품 코드와 상태로 상품권 목록을 조회합니다.
   *
   * @param productCode 상품 코드
   * @param status 상품권 상태
   * @param pageable 페이지네이션 정보
   * @return 조회된 상품권 프로젝션 목록
   */
  @Override
  public List<VoucherProjection> findAllVouchersByProductCode(
      String productCode, VoucherStatus status, Pageable pageable) {
    return mapper.toProjectionList(
        jpaRepository.findAllByProductCodeAndStatus(productCode, status, pageable));
  }

  /**
   * 상품 코드 목록과 상태로 상품권 수를 계산합니다.
   *
   * @param productCodes 상품 코드 목록
   * @param status 상품권 상태
   * @return 상품별 상품권 수 목록
   */
  @Override
  public List<ProductVoucherCount> countByProductCodesAndStatus(
      List<String> productCodes, VoucherStatus status) {
    return queryRepository.countByProductCodesAndStatus(productCodes, status);
  }

  /**
   * 상품권 ID 목록의 상태를 변경합니다.
   *
   * @param voucherIds 상태를 변경할 상품권 ID 목록
   * @param status 변경할 상품권 상태
   */
  @Override
  public void updateStatusToSold(List<Long> voucherIds, VoucherStatus status) {
    jpaRepository.updateStatusToSold(voucherIds, status);
  }

  /**
   * 소프트 삭제된 상품권을 복원합니다.
   *
   * @param voucher 복원할 상품권
   * @throws BusinessException 상품권을 찾을 수 없는 경우
   */
  @Override
  public void restore(Voucher voucher) {
    Voucher restoredVoucher =
        findById(voucher.getId())
            .map(
                v -> {
                  v.restore();
                  return v;
                })
            .orElseThrow(() -> new BusinessException(ErrorCode.VOUCHER_NOT_FOUND));
    save(restoredVoucher);
  }

  /**
   * ID로 소프트 삭제된 상품권을 복원합니다.
   *
   * @param id 복원할 상품권의 ID
   * @throws BusinessException 상품권을 찾을 수 없는 경우
   */
  @Override
  public void restoreById(Long id) {
    findById(id)
        .map(
            v -> {
              v.restore();
              return save(v);
            })
        .orElseThrow(() -> new BusinessException(ErrorCode.VOUCHER_NOT_FOUND));
  }

  /**
   * 상품권을 물리적으로 삭제합니다.
   *
   * @param voucher 삭제할 상품권
   */
  @Override
  public void delete(Voucher voucher) {
    jpaRepository.delete(mapper.toEntity(voucher));
  }

  /**
   * ID로 상품권을 물리적으로 삭제합니다.
   *
   * @param id 삭제할 상품권의 ID
   */
  @Override
  public void deleteById(Long id) {
    jpaRepository.deleteById(id);
  }

  /**
   * 상품권을 소프트 삭제합니다.
   *
   * @param voucher 소프트 삭제할 상품권
   * @throws BusinessException 상품권을 찾을 수 없는 경우
   */
  @Override
  public void softDelete(Voucher voucher) {
    Voucher deletedVoucher =
        findById(voucher.getId())
            .map(
                v -> {
                  v.softDelete();
                  return v;
                })
            .orElseThrow(() -> new BusinessException(ErrorCode.VOUCHER_NOT_FOUND));
    save(deletedVoucher);
  }

  /**
   * ID로 상품권을 소프트 삭제합니다.
   *
   * @param id 소프트 삭제할 상품권의 ID
   * @throws BusinessException 상품권을 찾을 수 없는 경우
   */
  @Override
  public void softDeleteById(Long id) {
    findById(id)
        .map(
            v -> {
              v.softDelete();
              return save(v);
            })
        .orElseThrow(() -> new BusinessException(ErrorCode.VOUCHER_NOT_FOUND));
  }
}
