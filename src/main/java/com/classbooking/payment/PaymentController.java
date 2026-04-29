package com.classbooking.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    // 결제
    @PatchMapping("/confirm")
    public ResponseEntity<Void> makePayment(
            @RequestHeader("X-Member-Id") Long memberId,
            @RequestBody PaymentRequest request) {
        paymentService.confirmPayment(memberId, request);
        return ResponseEntity.ok().build();
    }
}
