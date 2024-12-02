package kr.co.pincoin.api.external.payment.billgate;

import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@RequestMapping("/payment/billgate")
@RequiredArgsConstructor
@Slf4j
public class BillgatePaymentController {

  private final WebClient billgateWebClient;

  @Value("${billgate.webapi-url}")
  private String webapiUrl;

  @PostMapping("/return")
  public ResponseEntity<String> handleCallback(@Valid @ModelAttribute BillgateCallbackForm form) {
    try {
      // 빌게이트 승인 결과 확인
      BillgateApprovalResponse result = requestApproval(form);

      log.debug(
          "Payment approval response - orderId: {}, responseCode: {}, amount: {}",
          result.getOrderId(),
          result.getResponseCode(),
          result.getAuthAmount());

      if ("0000".equals(result.getResponseCode())) {
        // 1. 주문 조회

        // 2. 사용자 검증

        // 3. 결제금액 검증

        // 4. 테스트 계정 처리

        // 5. 상품권 발송 처리
      }

    } catch (Exception e) {
      log.error("Payment callback error", e);
    }

    return ResponseEntity.ok("<script>opener.location.reload(); self.close();</script>");
  }

  private BillgateApprovalResponse requestApproval(BillgateCallbackForm form) {
    Map<String, String> body =
        Map.of(
            "SERVICE_CODE", form.getServiceCode(),
            "SERVICE_ID", form.getServiceId(),
            "ORDER_ID", form.getOrderId(),
            "ORDER_DATE", form.getOrderDate(),
            "PAY_MESSAGE", form.getPayMessage());

    return billgateWebClient
        .post()
        .uri("/approve.jsp")
        .bodyValue(body)
        .retrieve()
        .bodyToMono(BillgateApprovalResponse.class)
        .block();
  }
}
