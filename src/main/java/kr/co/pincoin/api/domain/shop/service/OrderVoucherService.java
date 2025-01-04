package kr.co.pincoin.api.domain.shop.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import kr.co.pincoin.api.domain.shop.model.order.Order;
import kr.co.pincoin.api.domain.shop.model.order.OrderProduct;
import kr.co.pincoin.api.domain.shop.model.order.OrderProductVoucher;
import kr.co.pincoin.api.domain.shop.model.order.enums.OrderStatus;
import kr.co.pincoin.api.domain.shop.model.product.Voucher;
import kr.co.pincoin.api.domain.shop.model.product.enums.VoucherStatus;
import kr.co.pincoin.api.infra.shop.repository.order.projection.OrderProductVoucherCount;
import kr.co.pincoin.api.infra.shop.repository.product.projection.ProductVoucherCount;
import kr.co.pincoin.api.infra.shop.repository.product.projection.VoucherProjection;
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
    // 1. 사용자 인증
    // - SELECT User: 사용자 이메일로 로그인 정보 조회
    //
    // 2. 주문 결제 정보 저장
    // - INSERT OrderPayment: 결제 정보 저장
    // - SELECT OrderPayment: 총 결제 금액 조회
    // - UPDATE Order: 주문 상태 "입금확인" 변경
    //
    // 3. 발권 처리 시작
    // - SELECT OrderProductVoucher + OrderProduct 발권된 상품권 수량 확인
    // - SELECT Voucher + Product: 재고 상품권 수량 확인
    //
    // 4. 상품별 발권 처리 (n개 상품 for문)
    // - SELECT Voucher: 발권 가능한 상품권 조회
    // - UPDATE Voucher: 상태 "판매"로 변경
    // - UPDATE Product: 재고 수량 -x 차감
    //
    // 5. 발권 정보 저장 및 주문 완료 처리
    // - INSERT OrderProductVoucher: 주문-상품권 매핑 일괄 저장
    // - SELECT Order: 주문 정보 조회
    // - SELECT User: 주문자 정보 조회
    // - UPDATE Order: 주문 상태 "발송완료"로 변경

    // 상품별 주문 수량을 빠르게 조회하기 위한 Map 생성
    // key: 상품 코드, value: 주문 수량
    Map<String, Integer> quantityByCode = new HashMap<>();

    // 재고 조회를 위한 상품 코드 리스트
    List<String> productCodes = new ArrayList<>();

    // 주문 상품 목록을 순회하며 위의 두 자료구조 초기화
    orderProducts.forEach(
        product -> {
          quantityByCode.put(product.getCode(), product.getQuantity());
          productCodes.add(product.getCode());
        });

    // 중복 발권 방지를 위한 검증 로직
    // 주문 상태와 무관하게 실제 발권 여부를 확인
    // OrderProductVoucher 테이블에서 revoked=false인 레코드 존재 여부로 판단
    List<OrderProductVoucherCount> issuedCounts =
        orderProductVoucherPersistenceService.countIssuedVouchersByOrderProducts(orderProducts);

    // 이미 발권된 상품이 있다면 예외 발생
    // 에러 메시지에 상품 코드별 발권 수량 포함
    if (!issuedCounts.isEmpty()) {
      String message =
          "이미 발권 완료: "
              + issuedCounts.stream()
                  .map(count -> String.format("%s: %d개", count.productCode(), count.issuedCount()))
                  .collect(Collectors.joining(", "));

      throw new RuntimeException(message);
    }

    // 재고 수량 검증 로직
    // PURCHASED 상태인 상품권의 수량을 상품별로 조회
    List<ProductVoucherCount> productVoucherCounts =
        inventoryPersistenceService.countVouchersByProductCodesAndStatus(
            productCodes, VoucherStatus.PURCHASED);

    // 각 상품별로 주문 수량과 재고 수량 비교
    productVoucherCounts.forEach(
        (productCodeCount) -> {
          // 주문 수량이 재고 수량보다 많으면 예외 발생
          // 에러 메시지에 부족한 수량 정보 포함
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

    // 모든 검증이 완료된 후 실제 발권 처리 시작
    List<OrderProductVoucher> allVouchers = new ArrayList<>();

    for (OrderProduct orderProduct : orderProducts) {
      // 1. 발권 가능한 상품권 조회
      // - PURCHASED 상태인 상품권 중에서
      // - 주문 수량만큼만 조회
      List<VoucherProjection> availableVouchers =
          inventoryPersistenceService.findAvailableVouchers(
              orderProduct.getCode(), orderProduct.getQuantity());

      // 동시성 문제 - 경쟁상태(race condition): 두 개의 트랜잭션이 동시에 동일한 상품권을 조회하고 발권하려 할 때 문제 발생
      // Transaction A: findAvailableVouchers() -> 재고 100개 확인
      // Transaction B: findAvailableVouchers() -> 재고 100개 확인
      // Transaction A: updateStatusToSold() -> 50개 판매처리
      // Transaction B: updateStatusToSold() -> 동일한 50개 판매처리 (중복 판매)
      //
      // 해결책: 비관적 락 SELECT FOR UPDATE SKIP LOCKED

      // 2. 조회된 상품권들의 상태를 "판매"로 변경
      List<Long> voucherIds =
          availableVouchers.stream().map(VoucherProjection::id).collect(Collectors.toList());

      inventoryPersistenceService.updateStatusToSold(voucherIds);

      // 3. 주문 상품과 상품권을 연결하는 OrderProductVoucher 엔티티 생성 및 저장
      // OrderProductVoucher 엔티티 생성
      // - orderProduct, voucher 연결
      // - 상품권 코드와 비고 복사
      // - revoked 필드 초기값 = false
      List<OrderProductVoucher> orderProductVouchers =
          availableVouchers.stream()
              .map(
                  voucher ->
                      OrderProductVoucher.builder()
                          .orderProduct(OrderProduct.builder().id(orderProduct.getId()).build())
                          .voucher(Voucher.builder().id(voucher.id()).build())
                          .code(voucher.code())
                          .remarks(voucher.remarks())
                          .revoked(false)
                          .created(LocalDateTime.now())
                          .modified(LocalDateTime.now())
                          .isRemoved(false)
                          .build())
              .toList();

      allVouchers.addAll(orderProductVouchers);

      // 동시성 문제 - 갱신 손실(lost update): decreaseStockQuantity 호출 시 재고 차감에서 문제 발생
      // Transaction A: 재고 100개 읽음
      // Transaction B: 재고 100개 읽음
      // Transaction A: 50개 차감하여 50개로 업데이트
      // Transaction B: 30개 차감하여 70개로 업데이트 (A의 차감이 무시됨)
      //
      // 해결책: 자식 트랜잭션이 부모 트랜잭션에 참여하도록 명시
      // @Transactional(propagation = Propagation.REQUIRED)

      // 4. 상품의 재고 수량 차감
      inventoryPersistenceService.decreaseStockQuantity(
          orderProduct.getCode(), orderProduct.getQuantity());
    }

    orderProductVoucherPersistenceService.batchSave(allVouchers);

    // 모든 발권 처리가 완료되면 주문 상태를 "발송완료" 변경
    order.updateStatus(OrderStatus.SHIPPED);
    orderPersistenceService.save(order);
    return order;
  }
}
