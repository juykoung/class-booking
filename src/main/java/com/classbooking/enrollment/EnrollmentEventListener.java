package com.classbooking.enrollment;

import com.classbooking.enrollment.service.EnrollmentService;
import com.classbooking.payment.PaymentConfirmedEvent;
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
public class EnrollmentEventListener {
    private final EnrollmentService enrollmentService;

    @Retryable(
            retryFor = Exception.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePaymentConfirmed(PaymentConfirmedEvent event) {
        enrollmentService.confirmPayment(event.enrollmentId());
    }

    @Recover
    public void recover(Exception exception, PaymentConfirmedEvent event) {
        log.error(
                "[Enrollment] 수강 확정 실패. paymentId={}, enrollmentId={}",
                event.paymentId(),
                event.enrollmentId(),
                exception
        );
    }
}
