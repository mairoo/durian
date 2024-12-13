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
import kr.co.pincoin.api.infra.shop.entity.product.VoucherEntity;
import kr.co.pincoin.api.infra.shop.mapper.product.VoucherMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class VoucherRepositoryImpl implements VoucherRepository {

  private final VoucherJpaRepository jpaRepository;

  private final VoucherQueryRepository queryRepository;

  private final VoucherMapper mapper;

  @Override
  public Voucher save(Voucher voucher) {
    return mapper.toModel(jpaRepository.save(mapper.toEntity(voucher)));
  }

  @Override
  public List<Voucher> saveAll(Collection<Voucher> vouchers) {
    return mapper.toModelList(
        jpaRepository.saveAll(mapper.toEntityList(vouchers.stream().toList())));
  }

  @Override
  public Optional<Voucher> findById(Long id) {
    return jpaRepository.findById(id).map(mapper::toModel);
  }

  @Override
  public Optional<Voucher> findByCode(String code) {
    return jpaRepository.findByCode(code).map(mapper::toModel);
  }

  @Override
  public List<Voucher> findAllByIdIn(Collection<Long> ids) {
    return jpaRepository.findAllByIdIn(ids).stream()
        .map(mapper::toModel)
        .collect(Collectors.toList());
  }

  @Override
  public List<Voucher> findAllByCodeIn(Collection<String> codes) {
    return jpaRepository.findAllByCodeIn(codes).stream()
        .map(mapper::toModel)
        .collect(Collectors.toList());
  }

  @Override
  public List<Voucher> findTopNByProductCodeAndStatusOrderByIdAsc(
      String productCode, VoucherStatus status, int limit) {
    List<VoucherEntity> savedEntities =
        jpaRepository.findTopNByProductCodeAndStatusOrderByIdAsc(productCode, status, limit);

    return mapper.toModelList(savedEntities);
  }

  @Override
  public void delete(Voucher voucher) {
    jpaRepository.delete(mapper.toEntity(voucher));
  }

  @Override
  public void deleteById(Long id) {
    jpaRepository.deleteById(id);
  }

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
