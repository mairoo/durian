package kr.co.pincoin.api.infra.shop.service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import kr.co.pincoin.api.domain.shop.model.product.Product;
import kr.co.pincoin.api.domain.shop.model.product.Voucher;
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
        return productRepository.findById(id);
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
}