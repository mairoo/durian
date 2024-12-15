package kr.co.pincoin.api.app.member.payment.controller;

import jakarta.validation.Valid;
import kr.co.pincoin.api.app.member.payment.request.BankTransferRequest;
import kr.co.pincoin.api.domain.shop.service.OrderPaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment/bank-transfer")
@RequiredArgsConstructor
@Slf4j
public class BankTransferPaymentController {

    private OrderPaymentService orderPaymentService;

    @PostMapping("/bank-transfer/callback")
    public ResponseEntity<Void> handleBankTransferCallback(
        @Valid BankTransferRequest request) {

        log.info("Bank transfer callback received - account: {}, name: {}, amount: {}",
            request.getAccount(),
            request.getName(),
            request.getAmount());

        return ResponseEntity.ok().build();
    }
}
