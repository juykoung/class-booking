package com.classbooking.payment;

import com.classbooking.enrollment.service.EnrollmentWithdrawnEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventListener {
    private final PaymentService paymentService;

    @Retryable(
            retryFor = Exception.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleEnrollmentWithdrawn(EnrollmentWithdrawnEvent event) {
        paymentService.refundPayment(event.enrollmentId());
    }

    @Recover
    public void recover(Exception exception, EnrollmentWithdrawnEvent event) {
        log.error(
                "[Payment] 환불 처리 실패. 수강신청 ID: {}",
                event.enrollmentId(),
                exception
        );
    }
}
