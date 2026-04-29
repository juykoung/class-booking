package com.classbooking.payment.controller;

import com.classbooking.payment.dto.PaymentRequest;
import com.classbooking.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
@Tag(name = "Payment", description = "결제 확정 API")
public class PaymentController {
    private final PaymentService paymentService;

    @Operation(summary = "결제 확정", description = "수강 신청 건을 결제합니다. 결제 성공 후 이벤트로 수강 상태를 확정합니다.")
    @PatchMapping("/confirm")
    public ResponseEntity<Void> makePayment(
            @Parameter(description = "결제자 회원 ID", example = "2")
            @RequestHeader("X-Member-Id") Long memberId,
            @RequestBody PaymentRequest request
    ) {
        paymentService.confirmPayment(memberId, request);
        return ResponseEntity.ok().build();
    }
}
