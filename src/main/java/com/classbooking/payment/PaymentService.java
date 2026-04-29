package com.classbooking.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final ApplicationEventPublisher eventPublisher;
    private final PaymentRepository paymentRepository;

    @Transactional
    public void confirmPayment(Long memberId, PaymentRequest request) {
        Payment payment = new Payment(
                memberId,
                request.enrollmentId(),
                request.amount(),
                PaymentStatus.SUCCESS
        );

         paymentRepository.save(payment);

        log.info("[Payment] 결제 확정. memberId={}, lectureId={}, amount={}", memberId, request.enrollmentId(), request.amount());
        eventPublisher.publishEvent(new PaymentConfirmedEvent(payment.getId(), payment.getEnrollmentId()));
    }
}
