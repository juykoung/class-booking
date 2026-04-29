package com.classbooking.payment.controller;

import com.classbooking.payment.service.PaymentService;
import com.classbooking.payment.dto.PaymentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
