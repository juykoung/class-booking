package com.classbooking.payment;

import com.classbooking.enrollment.dto.Enrollment;
import com.classbooking.enrollment.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final ApplicationEventPublisher eventPublisher;
    private final PaymentRepository paymentRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Transactional
    public void confirmPayment(Long memberId, PaymentRequest request) {
        Enrollment enrollment = enrollmentRepository.findById(request.enrollmentId())
                .orElseThrow(() -> new IllegalArgumentException("수강신청이 존재하지 않습니다."));

        enrollment.validatePayable(memberId);

        Payment payment = new Payment(
                memberId,
                request.enrollmentId(),
                request.amount(),
                PaymentStatus.SUCCESS
        );

        paymentRepository.save(payment);

        log.info("[Payment] Payment confirmed. memberId={}, enrollmentId={}, amount={}",
                memberId,
                request.enrollmentId(),
                request.amount());
        eventPublisher.publishEvent(new PaymentConfirmedEvent(payment.getId(), payment.getEnrollmentId()));
    }
}
