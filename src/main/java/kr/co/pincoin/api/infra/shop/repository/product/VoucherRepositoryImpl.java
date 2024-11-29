package kr.co.pincoin.api.infra.shop.repository.product;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import kr.co.pincoin.api.domain.shop.model.product.Voucher;
import kr.co.pincoin.api.domain.shop.model.product.enums.VoucherStatus;
import kr.co.pincoin.api.domain.shop.repository.product.VoucherRepository;
import kr.co.pincoin.api.infra.shop.entity.product.VoucherEntity;
import kr.co.pincoin.api.infra.shop.mapper.product.VoucherMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class VoucherRepositoryImpl implements VoucherRepository {
  private final VoucherJpaRepository voucherJpaRepository;

  private final VoucherQueryRepository voucherQueryRepository;

  private final VoucherMapper voucherMapper;

  @Override
  public Voucher save(Voucher voucher) {
    return voucherMapper.toModel(voucherJpaRepository.save(voucherMapper.toEntity(voucher)));
  }

  @Override
  public List<Voucher> saveAll(Collection<Voucher> vouchers) {
    return voucherMapper.toModelList(
        voucherJpaRepository.saveAll(voucherMapper.toEntityList(vouchers.stream().toList())));
  }

  @Override
  public Optional<Voucher> findById(Long id) {
    return voucherJpaRepository.findById(id).map(voucherMapper::toModel);
  }

  @Override
  public Optional<Voucher> findByCode(String code) {
    return voucherJpaRepository.findByCode(code).map(voucherMapper::toModel);
  }

  @Override
  public List<Voucher> findAllByIdIn(Collection<Long> ids) {
    return voucherJpaRepository.findAllByIdIn(ids).stream()
        .map(voucherMapper::toModel)
        .collect(Collectors.toList());
  }

  @Override
  public List<Voucher> findAllByCodeIn(Collection<String> codes) {
    return voucherJpaRepository.findAllByCodeIn(codes).stream()
        .map(voucherMapper::toModel)
        .collect(Collectors.toList());
  }

  @Override
  public List<Voucher> findTopNByProductCodeAndStatusOrderByIdAsc(
      String productCode, VoucherStatus status, int limit) {
    List<VoucherEntity> savedEntities =
        voucherJpaRepository.findTopNByProductCodeAndStatusOrderByIdAsc(productCode, status, limit);

    return voucherMapper.toModelList(savedEntities);
  }

  @Override
  public boolean existsByCode(String code) {
    return voucherJpaRepository.findByCode(code).isPresent();
  }

  @Override
  public void delete(Voucher voucher) {
    voucherJpaRepository.delete(voucherMapper.toEntity(voucher));
  }

  @Override
  public void deleteById(Long id) {
    voucherJpaRepository.deleteById(id);
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
            .orElseThrow(() -> new IllegalArgumentException("Voucher not found"));
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
        .orElseThrow(() -> new IllegalArgumentException("Voucher not found"));
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
            .orElseThrow(() -> new IllegalArgumentException("Voucher not found"));
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
        .orElseThrow(() -> new IllegalArgumentException("Voucher not found"));
  }
}
