package kr.co.pincoin.api.domain.shop.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import kr.co.pincoin.api.app.member.order.request.CartItem;
import kr.co.pincoin.api.domain.shop.model.order.Order;
import kr.co.pincoin.api.domain.shop.model.order.OrderProduct;
import kr.co.pincoin.api.domain.shop.model.order.OrderProductVoucher;
import kr.co.pincoin.api.domain.shop.model.product.Product;
import kr.co.pincoin.api.domain.shop.model.product.Voucher;
import kr.co.pincoin.api.infra.shop.service.OrderPersistenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderVoucherService {

  private final OrderPersistenceService persistenceService;

//  public List<OrderProductVoucher> getOrderProductVouchers(Order order) {
//    return persistenceService.findOrderProductVouchers(order.getId());
//  }

  /** 주문에 대한 바우처를 발행한다. */
  @Transactional
  public Order issueVouchers(Order order) {
    List<OrderProduct> orderProducts = persistenceService.findOrderProductsWithOrder(order);

    List<OrderProductVoucher> allVouchers = new ArrayList<>();
    List<Voucher> vouchersToUpdate = new ArrayList<>();
    List<Product> productsToUpdate = new ArrayList<>();

    for (OrderProduct orderProduct : orderProducts) {
      processVoucherIssue(orderProduct, allVouchers, vouchersToUpdate, productsToUpdate);
    }

    persistenceService.saveOrderProductVouchersBatch(allVouchers);
    persistenceService.updateVouchersBatch(vouchersToUpdate);
    persistenceService.updateProductsBatch(productsToUpdate);

    return order;
  }

  /** 발행된 바우처를 취소한다. */
//  @Transactional
//  public void revokeVouchers(Long orderId) {
//    List<OrderProductVoucher> vouchers = persistenceService.findOrderProductVouchers(orderId);
//
//    vouchers.forEach(
//        voucher -> {
//          voucher.revoke();
//
//          Voucher originalVoucher =
//              persistenceService
//                  .findVoucherByCode(voucher.getCode())
//                  .orElseThrow(
//                      () -> new EntityNotFoundException("상품권을 찾을 수 없습니다: " + voucher.getCode()));
//
//          originalVoucher.markAsPurchased();
//          persistenceService.updateVoucher(originalVoucher);
//        });
//
//    persistenceService.saveOrderProductVouchers(vouchers);
//  }

  /** 주문 상품별 바우처 발행을 처리한다. */
  private void processVoucherIssue(
      OrderProduct orderProduct,
      List<OrderProductVoucher> allVouchers,
      List<Voucher> vouchersToUpdate,
      List<Product> productsToUpdate) {

    Product product = validateProductForVoucherIssue(orderProduct);

    List<Voucher> availableVouchers =
        persistenceService.findAvailableVouchers(
            orderProduct.getCode(), orderProduct.getQuantity());

    validateVouchersAvailability(orderProduct, availableVouchers, product);

    for (Voucher voucher : availableVouchers) {
      voucher.markAsSold();
      vouchersToUpdate.add(voucher);

      allVouchers.add(
          OrderProductVoucher.builder()
              .orderProduct(orderProduct)
              .code(voucher.getCode())
              .remarks(voucher.getRemarks())
              .revoked(false)
              .build());
    }

    product.updateStockQuantity(product.getStockQuantity() - orderProduct.getQuantity());
    productsToUpdate.add(product);
  }

  /** 바우처 발행을 위한 상품 검증 */
  private Product validateProductForVoucherIssue(OrderProduct orderProduct) {
    return persistenceService
        .findProductsByCartItems(List.of(CartItem.from(orderProduct)))
        .stream()
        .findFirst()
        .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다: " + orderProduct.getCode()));
  }

  /** 바우처 발행 가능 여부를 검증한다. */
  private void validateVouchersAvailability(
      OrderProduct orderProduct, List<Voucher> vouchers, Product product) {

    if (vouchers.size() < orderProduct.getQuantity()) {
      throw new IllegalStateException(
          String.format(
              "상품 '%s'의 사용 가능한 상품권이 부족합니다. 필요: %d, 가용: %d",
              orderProduct.getName(), orderProduct.getQuantity(), vouchers.size()));
    }

    if (product.getStockQuantity() < vouchers.size()) {
      throw new IllegalStateException(
          String.format(
              "상품 '%s'의 재고와 상품권 수량이 불일치합니다. 재고: %d, 상품권: %d",
              product.getName(), product.getStockQuantity(), vouchers.size()));
    }
  }

  /** 주문의 바우처 발행 상태를 조회한다. */
  public boolean hasIssuedVouchers(Long orderId) {
    return !persistenceService.findOrderProductVouchers(orderId).isEmpty();
  }

  /** 주문의 바우처가 모두 취소되었는지 확인한다. */
//  public boolean allVouchersRevoked(Long orderId) {
//    List<OrderProductVoucher> vouchers = persistenceService.findOrderProductVouchers(orderId);
//    return !vouchers.isEmpty() && vouchers.stream().allMatch(OrderProductVoucher::getRevoked);
//  }

  /** 특정 바우처 코드가 주문에 속해있는지 확인한다. */
//  public boolean isVoucherBelongToOrder(String voucherCode, Long orderId) {
//    return persistenceService.findOrderProductVouchers(orderId).stream()
//        .anyMatch(v -> v.getCode().equals(voucherCode));
//  }
}
