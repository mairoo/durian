package kr.co.pincoin.api.domain.shop.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import kr.co.pincoin.api.domain.shop.model.order.Order;
import kr.co.pincoin.api.domain.shop.model.order.OrderProduct;
import kr.co.pincoin.api.domain.shop.model.order.OrderProductVoucher;
import kr.co.pincoin.api.domain.shop.model.product.Voucher;
import kr.co.pincoin.api.domain.shop.model.product.enums.VoucherStatus;
import kr.co.pincoin.api.infra.shop.repository.order.projection.OrderProductVoucherCount;
import kr.co.pincoin.api.infra.shop.repository.product.projection.ProductVoucherCount;
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
    // 상품 코드와 주문 수량 맵 생성
    Map<String, Integer> quantityByCode = new HashMap<>();

    // 상품 코드 목록 생성
    List<String> productCodes = new ArrayList<>();

    orderProducts.forEach(
        product -> {
          quantityByCode.put(product.getCode(), product.getQuantity());
          productCodes.add(product.getCode());
        });

    // 이미 발송되었는지 확인
    // - 주문 상태는 입금완료, 입금인증완료 등 미발송 상태이지만
    // - 실제로는 발권 되고 주문 상태 업데이트가 안 된 경우 대비
    // - OrderProduct를 참조하는 OrderProductVoucher가 존재하고 revoked=false라면 발권이 완료된 상태
    List<OrderProductVoucherCount> issuedCounts =
        orderProductVoucherPersistenceService.countIssuedVouchersByOrderProducts(orderProducts);

    if (!issuedCounts.isEmpty()) {
      String message =
          "이미 발권 완료: "
              + issuedCounts.stream()
                  .map(
                      count ->
                          String.format("%s: %d개", count.getProductCode(), count.getIssuedCount()))
                  .collect(Collectors.joining(", "));

      throw new RuntimeException(message);
    }

    // 재고수량 검증
    List<ProductVoucherCount> productVoucherCounts =
        inventoryPersistenceService.countVouchersByProductCodesAndStatus(
            productCodes, VoucherStatus.PURCHASED);

    productVoucherCounts.forEach(
        (productCodeCount) -> {
          log.warn(
              "코드: {}, 남은수량: {}",
              productCodeCount.getProductCode(),
              productCodeCount.getAvailableCount());

          Integer orderQuantity = quantityByCode.get(productCodeCount.getProductCode());
          if (orderQuantity > productCodeCount.getAvailableCount()) {
            throw new RuntimeException(
                String.format(
                    "%s 재고 부족: %d - %d = %d",
                    productCodeCount.getProductCode(),
                    productCodeCount.getAvailableCount(),
                    orderQuantity,
                    productCodeCount.getAvailableCount() - orderQuantity));
          }
        });

    // 실제 발권 처리
    for (OrderProduct orderProduct : orderProducts) {
      // 1. 구매 가능한 상품권 코드 조회 (매입 상태 & 주문 수량 만큼)
      List<Voucher> availableVouchers =
          inventoryPersistenceService.findAvailableVouchersByProductCode(
              orderProduct.getCode(), orderProduct.getQuantity());

      // 2. 조회된 상품권 코드의 상태를 "판매"로 변경
      List<Long> voucherIds =
          availableVouchers.stream().map(Voucher::getId).collect(Collectors.toList());

      inventoryPersistenceService.updateStatusToSold(voucherIds);

      // 3. OrderProductVoucher 생성 및 벌크 저장
      List<OrderProductVoucher> orderProductVouchers =
          availableVouchers.stream()
              .map(
                  voucher ->
                      OrderProductVoucher.builder()
                          .orderProduct(orderProduct)
                          .voucher(voucher)
                          .code(voucher.getCode())
                          .remarks(voucher.getRemarks())
                          .revoked(false)
                          .build())
              .collect(Collectors.toList());

      orderProductVoucherPersistenceService.saveAll(orderProductVouchers);

      // 4. 상품 재고 감소
      inventoryPersistenceService.decreaseStockQuantity(
          orderProduct.getCode(), orderProduct.getQuantity());
    }

    throw new RuntimeException("그냥 종료하자.");
  }
}
