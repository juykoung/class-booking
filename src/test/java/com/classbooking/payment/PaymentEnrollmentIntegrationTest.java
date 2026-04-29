package com.classbooking.payment;

import com.classbooking.enrollment.dto.Enrollment;
import com.classbooking.enrollment.dto.EnrollmentStatus;
import com.classbooking.enrollment.repository.EnrollmentRepository;
import com.classbooking.enrollment.service.EnrollmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class PaymentEnrollmentIntegrationTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private EnrollmentService enrollmentService;

    @BeforeEach
    void setUp() {
        paymentRepository.deleteAll();
        enrollmentRepository.deleteAll();
    }

    @Test
    @DisplayName("PENDING 상태 수강 결제 성공")
    void paymentSuccessConfirmsPendingEnrollment() {
        Long memberId = 1L;
        Enrollment enrollment = enrollmentRepository.save(new Enrollment(memberId, 10L, EnrollmentStatus.PENDING));
        PaymentRequest request = new PaymentRequest(enrollment.getId(), BigDecimal.valueOf(50_000));

        paymentService.confirmPayment(memberId, request);

        List<Payment> payments = paymentRepository.findAll();
        assertThat(payments).hasSize(1);
        assertThat(payments.get(0).getMemberId()).isEqualTo(memberId);
        assertThat(payments.get(0).getEnrollmentId()).isEqualTo(enrollment.getId());
        assertThat(payments.get(0).getStatus()).isEqualTo(PaymentStatus.SUCCESS);

        Enrollment confirmedEnrollment = enrollmentRepository.findById(enrollment.getId()).orElseThrow();
        assertThat(confirmedEnrollment.getStatus()).isEqualTo(EnrollmentStatus.CONFIRMED);
        assertThat(confirmedEnrollment.getPaymentDate()).isNotNull();
        assertThat(confirmedEnrollment.getCancelDeadline()).isNotNull();
    }

    @Test
    @DisplayName("WAITLISTED 상태 수강 결제 실패")
    void paymentFailsBeforeSavingWhenEnrollmentIsNotPayable() {
        Long memberId = 1L;
        Enrollment waitlistedEnrollment = enrollmentRepository.save(
                new Enrollment(memberId, 10L, EnrollmentStatus.WAITLISTED)
        );
        PaymentRequest request = new PaymentRequest(waitlistedEnrollment.getId(), BigDecimal.valueOf(50_000));

        assertThatThrownBy(() -> paymentService.confirmPayment(memberId, request))
                .isInstanceOf(IllegalStateException.class);

        assertThat(paymentRepository.findAll()).isEmpty();

        Enrollment unchangedEnrollment = enrollmentRepository.findById(waitlistedEnrollment.getId()).orElseThrow();
        assertThat(unchangedEnrollment.getStatus()).isEqualTo(EnrollmentStatus.WAITLISTED);
        assertThat(unchangedEnrollment.getPaymentDate()).isNull();
        assertThat(unchangedEnrollment.getCancelDeadline()).isNull();
    }

    @Test
    @DisplayName("결제 마감일이 지난 수강건의 결제 실패")
    void paymentFailsBeforeSavingWhenPayDeadlineHasPassed() {
        Long memberId = 1L;
        Enrollment enrollment = new Enrollment(memberId, 10L, EnrollmentStatus.PENDING);
        ReflectionTestUtils.setField(enrollment, "payDeadline", LocalDateTime.now().minusSeconds(1));
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        PaymentRequest request = new PaymentRequest(savedEnrollment.getId(), BigDecimal.valueOf(50_000));

        assertThatThrownBy(() -> paymentService.confirmPayment(memberId, request))
                .isInstanceOf(IllegalStateException.class);

        assertThat(paymentRepository.findAll()).isEmpty();

        Enrollment unchangedEnrollment = enrollmentRepository.findById(savedEnrollment.getId()).orElseThrow();
        assertThat(unchangedEnrollment.getStatus()).isEqualTo(EnrollmentStatus.PENDING);
        assertThat(unchangedEnrollment.getPaymentDate()).isNull();
        assertThat(unchangedEnrollment.getCancelDeadline()).isNull();
    }

    @Test
    @DisplayName("결제된 수강건 수강 철회 시 환불 처리")
    void enrollmentWithdrawalRefundsSuccessfulPayment() {
        Long memberId = 1L;
        Enrollment enrollment = enrollmentRepository.save(new Enrollment(memberId, 10L, EnrollmentStatus.PENDING));
        PaymentRequest request = new PaymentRequest(enrollment.getId(), BigDecimal.valueOf(50_000));
        paymentService.confirmPayment(memberId, request);

        enrollmentService.withdraw(memberId, enrollment.getId());

        Enrollment withdrawnEnrollment = enrollmentRepository.findById(enrollment.getId()).orElseThrow();
        assertThat(withdrawnEnrollment.getStatus()).isEqualTo(EnrollmentStatus.CANCELLED);
        assertThat(withdrawnEnrollment.getCancelledAt()).isNotNull();

        Payment refundedPayment = paymentRepository.findByEnrollmentIdAndStatus(
                enrollment.getId(),
                PaymentStatus.REFUNDED
        ).orElseThrow();
        assertThat(refundedPayment.getRefundedAt()).isNotNull();
    }
}
