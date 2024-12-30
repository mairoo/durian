package kr.co.pincoin.api.domain.shop.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import kr.co.pincoin.api.domain.shop.model.order.Order;
import kr.co.pincoin.api.domain.shop.model.order.OrderProduct;
import kr.co.pincoin.api.domain.shop.model.order.OrderProductVoucher;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderStatus;
import kr.co.pincoin.api.domain.shop.model.product.Product;
import kr.co.pincoin.api.domain.shop.model.product.Voucher;
import kr.co.pincoin.api.infra.shop.service.CatalogPersistenceService;
import kr.co.pincoin.api.infra.shop.service.InventoryPersistenceService;
import kr.co.pincoin.api.infra.shop.service.OrderPersistenceService;
import kr.co.pincoin.api.infra.shop.service.OrderProductPersistenceService;
import kr.co.pincoin.api.infra.shop.service.OrderProductVoucherPersistenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/** 주문에 대한 상품권 발행과 관리를 담당하는 서비스 - 상품권 발행, 검증, 상태 조회 등의 기능을 제공 - 트랜잭션 관리와 영속성 계층과의 상호작용을 처리 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class OrderVoucherService {

  private final OrderPersistenceService orderPersistenceService;

  private final OrderProductPersistenceService orderProductPersistenceService;

  private final OrderProductVoucherPersistenceService orderProductVoucherPersistenceService;

  private final InventoryPersistenceService inventoryPersistenceService;

  private final CatalogPersistenceService catalogPersistenceService;

  /**
   * 주문에 대한 상품권을 발행한다.
   *
   * @param order 상품권을 발행할 주문
   * @return 상품권이 발행된 주문
   */
  @Transactional(propagation = Propagation.REQUIRED, timeout = 30)
  public Order issueVouchers(Order order) {
    // 주문에 속한 모든 상품을 조회
    List<OrderProduct> orderProducts =
        orderProductPersistenceService.findOrderProductsWithOrder(order);

    return issueVouchers(order, orderProducts);
  }

  /**
   * 주문과 주문 상품 목록을 기반으로 상품권을 발행한다.
   *
   * @param order 상품권을 발행할 주문
   * @param orderProducts 상품권을 발행할 주문 상품 목록
   * @return 상품권이 발행된 주문
   */
  @Transactional(propagation = Propagation.REQUIRED, timeout = 30)
  public Order issueVouchers(Order order, List<OrderProduct> orderProducts) {
    // 상품 코드와 필요 수량 맵 생성
    Map<String, Integer> quantityByCode =
        orderProducts.stream()
            .collect(Collectors.toMap(OrderProduct::getCode, OrderProduct::getQuantity));

    // 모든 상품의 상품권을 한 번에 조회
    Map<String, List<Voucher>> vouchersByProduct =
        inventoryPersistenceService.findAvailableVouchersByProductCodes(
            quantityByCode.keySet(), quantityByCode);

    List<OrderProductVoucher> allVouchers = new ArrayList<>();
    List<Voucher> vouchersToUpdate = new ArrayList<>();
    List<Product> productsToUpdate = new ArrayList<>();

    // 각 주문 상품에 대한 처리
    for (OrderProduct orderProduct : orderProducts) {
      List<Voucher> availableVouchers = vouchersByProduct.get(orderProduct.getCode());
      if (availableVouchers == null || availableVouchers.size() < orderProduct.getQuantity()) {
        throw new IllegalStateException(
            String.format(
                "상품 '%s'의 사용 가능한 상품권이 부족합니다. 필요: %d, 가용: %d",
                orderProduct.getName(),
                orderProduct.getQuantity(),
                availableVouchers == null ? 0 : availableVouchers.size()));
      }

      Product product = availableVouchers.getFirst().getProduct();
      validateVouchersAvailability(orderProduct, availableVouchers, product);

      processVoucherIssue(
          orderProduct,
          product,
          allVouchers,
          vouchersToUpdate,
          productsToUpdate,
          vouchersByProduct);
    }

    orderProductVoucherPersistenceService.saveOrderProductVouchersBatch(allVouchers);
    inventoryPersistenceService.updateVouchersBatch(vouchersToUpdate);
    catalogPersistenceService.updateProductsBatch(productsToUpdate);

    order.updateStatus(OrderStatus.SHIPPED);
    orderPersistenceService.save(order);

    return order;
  }

  /**
   * 주문 상품별 상품권 발행을 처리한다.
   *
   * @param orderProduct 상품권을 발행할 주문 상품
   * @param product 주문 상품에 해당하는 상품 정보
   * @param allVouchers 발행된 모든 상품권을 담을 리스트
   * @param vouchersToUpdate 업데이트할 상품권 리스트
   * @param productsToUpdate 업데이트할 상품 리스트
   * @throws EntityNotFoundException 상품을 찾을 수 없는 경우
   * @throws IllegalStateException 상품권 발행이 불가능한 경우
   */
  private void processVoucherIssue(
      OrderProduct orderProduct,
      Product product,
      List<OrderProductVoucher> allVouchers,
      List<Voucher> vouchersToUpdate,
      List<Product> productsToUpdate,
      Map<String, List<Voucher>> vouchersByProduct) {

    if (product == null) {
      throw new EntityNotFoundException("상품을 찾을 수 없습니다: " + orderProduct.getCode());
    }

    // 이미 조회된 상품권 목록 사용
    List<Voucher> availableVouchers = vouchersByProduct.get(orderProduct.getCode());

    // 각 상품권에 대한 처리
    for (Voucher voucher : availableVouchers) {
      voucher.markAsSold();
      vouchersToUpdate.add(voucher);

      allVouchers.add(
          OrderProductVoucher.builder()
              .orderProduct(orderProduct)
              .voucher(voucher)
              .code(voucher.getCode())
              .remarks(voucher.getRemarks())
              .revoked(false)
              .build());
    }

    product.updateStockQuantity(product.getStockQuantity() - orderProduct.getQuantity());
    productsToUpdate.add(product);
  }

  /**
   * 상품권 발행 가능 여부를 검증한다.
   *
   * @param orderProduct 검증할 주문 상품
   * @param vouchers 발행 가능한 상품권 목록
   * @param product 상품 정보
   * @throws IllegalStateException 상품권 발행이 불가능한 경우
   */
  private void validateVouchersAvailability(
      OrderProduct orderProduct, List<Voucher> vouchers, Product product) {

    // 필요한 수량만큼의 상품권이 있는지 검증
    if (vouchers.size() < orderProduct.getQuantity()) {
      throw new IllegalStateException(
          String.format(
              "상품 '%s'의 사용 가능한 상품권이 부족합니다. 필요: %d, 가용: %d",
              orderProduct.getName(), orderProduct.getQuantity(), vouchers.size()));
    }

    // 상품 재고와 상품권 수량이 일치하는지 검증
    if (product.getStockQuantity() < vouchers.size()) {
      throw new IllegalStateException(
          String.format(
              "상품 '%s'의 재고와 상품권 수량이 불일치합니다. 재고: %d, 상품권: %d",
              product.getName(), product.getStockQuantity(), vouchers.size()));
    }
  }

  /**
   * 주문의 상품권 발행 상태를 조회한다.
   *
   * @param orderId 조회할 주문 ID
   * @return 상품권 발행 여부
   */
  public boolean hasIssuedVouchers(Long orderId) {
    return !orderProductVoucherPersistenceService.findOrderProductVouchers(orderId).isEmpty();
  }

  /** 발행된 상품권을 취소한다. */
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
  //                      () -> new EntityNotFoundException("상품권을 찾을 수 없습니다: " +
  // voucher.getCode()));
  //
  //          originalVoucher.markAsPurchased();
  //          persistenceService.updateVoucher(originalVoucher);
  //        });
  //
  //    persistenceService.saveOrderProductVouchers(vouchers);
  //  }

  /** 주문의 상품권이 모두 취소되었는지 확인한다. */
  //  public boolean allVouchersRevoked(Long orderId) {
  //    List<OrderProductVoucher> vouchers = persistenceService.findOrderProductVouchers(orderId);
  //    return !vouchers.isEmpty() && vouchers.stream().allMatch(OrderProductVoucher::getRevoked);
  //  }

  /** 특정 상품권 코드가 주문에 속해있는지 확인한다. */
  //  public boolean isVoucherBelongToOrder(String voucherCode, Long orderId) {
  //    return persistenceService.findOrderProductVouchers(orderId).stream()
  //        .anyMatch(v -> v.getCode().equals(voucherCode));
  //  }
}
