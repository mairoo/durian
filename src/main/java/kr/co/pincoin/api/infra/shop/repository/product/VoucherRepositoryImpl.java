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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class VoucherRepositoryImpl implements VoucherRepository {

  private final VoucherJpaRepository jpaRepository;
  private final VoucherQueryRepository queryRepository;
  private final VoucherMapper mapper;

  /**
   * 바우처를 생성하거나 수정합니다
   *
   * @param voucher 저장할 바우처
   * @return 저장된 바우처
   */
  @Override
  public Voucher save(Voucher voucher) {
    return mapper.toModel(jpaRepository.save(mapper.toEntity(voucher)));
  }

  /**
   * 여러 바우처를 일괄 저장합니다
   *
   * @param vouchers 저장할 바우처 목록
   * @return 저장된 바우처 목록
   */
  @Override
  public List<Voucher> saveAll(Collection<Voucher> vouchers) {
    return mapper.toModelList(
        jpaRepository.saveAll(mapper.toEntityList(vouchers.stream().toList())));
  }

  /**
   * ID로 바우처를 조회합니다
   *
   * @param id 바우처 ID
   * @return 조회된 바우처 (없을 경우 Optional.empty)
   */
  @Override
  public Optional<Voucher> findById(Long id) {
    return jpaRepository.findById(id).map(mapper::toModel);
  }

  /**
   * 코드로 바우처를 조회합니다
   *
   * @param code 바우처 코드
   * @return 조회된 바우처 (없을 경우 Optional.empty)
   */
  @Override
  public Optional<Voucher> findByCode(String code) {
    return jpaRepository.findByCode(code).map(mapper::toModel);
  }

  /**
   * ID 목록으로 여러 바우처를 조회합니다
   *
   * @param ids 바우처 ID 목록
   * @return 조회된 바우처 목록
   */
  @Override
  public List<Voucher> findAllByIdIn(Collection<Long> ids) {
    return jpaRepository.findAllByIdIn(ids).stream()
        .map(mapper::toModel)
        .collect(Collectors.toList());
  }

  /**
   * 코드 목록으로 여러 바우처를 조회합니다
   *
   * @param codes 바우처 코드 목록
   * @return 조회된 바우처 목록
   */
  @Override
  public List<Voucher> findAllByCodeIn(Collection<String> codes) {
    return jpaRepository.findAllByCodeIn(codes).stream()
        .map(mapper::toModel)
        .collect(Collectors.toList());
  }

  @Override
  public List<Voucher> findAllByProductCodesAndVoucherStatus(
      List<String> productCodes, VoucherStatus status) {
    return mapper.toModelList(
        queryRepository.findAllByProductCodesAndVoucherStatus(productCodes, status));
  }

  @Override
  public List<ProductVoucherCount> countByProductCodesAndStatus(
      List<String> productCodes, VoucherStatus status) {
    return queryRepository.countByProductCodesAndStatus(productCodes, status);
  }

  /**
   * 바우처를 삭제합니다
   *
   * @param voucher 삭제할 바우처
   */
  @Override
  public void delete(Voucher voucher) {
    jpaRepository.delete(mapper.toEntity(voucher));
  }

  /**
   * ID로 바우처를 삭제합니다
   *
   * @param id 삭제할 바우처의 ID
   */
  @Override
  public void deleteById(Long id) {
    jpaRepository.deleteById(id);
  }

  /**
   * 바우처를 소프트 삭제합니다
   *
   * @param voucher 소프트 삭제할 바우처
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
   * ID로 바우처를 소프트 삭제합니다
   *
   * @param id 소프트 삭제할 바우처의 ID
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

  /**
   * 소프트 삭제된 바우처를 복원합니다
   *
   * @param voucher 복원할 바우처
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
   * ID로 소프트 삭제된 바우처를 복원합니다
   *
   * @param id 복원할 바우처의 ID
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
}
