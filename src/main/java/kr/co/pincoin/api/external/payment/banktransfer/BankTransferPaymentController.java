package kr.co.pincoin.api.external.payment.banktransfer;

import jakarta.validation.Valid;
import kr.co.pincoin.api.domain.shop.service.OrderPaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment/bank-transfer")
@RequiredArgsConstructor
@Slf4j
public class BankTransferPaymentController {

    private OrderPaymentService orderPaymentService;

    @PostMapping("/callback")
    public ResponseEntity<Void> handleBankTransferCallback(
        @Valid @ModelAttribute BankTransferRequest request) {

        log.debug(
            "Bank transfer webhook received: account={}, received={}, name={}, method={}, amount={}, balance={}",
            request.getAccount(),
            request.getReceived(),
            request.getName(),
            request.getMethod(),
            request.getAmount(),
            request.getBalance());

        return ResponseEntity.ok().build();
    }
}
