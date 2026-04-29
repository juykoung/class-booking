package com.classbooking.payment;

import com.classbooking.enrollment.dto.Enrollment;
import com.classbooking.enrollment.dto.EnrollmentStatus;
import com.classbooking.enrollment.repository.EnrollmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest
class PaymentEnrollmentIntegrationTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @BeforeEach
    void setUp() {
        paymentRepository.deleteAll();
        enrollmentRepository.deleteAll();
    }

    @Test
    @DisplayName("결제 성공, 수강 확정 성공")
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
    @DisplayName("결제 성공, 수강 확정 실패")
    void paymentRemainsSuccessfulWhenEnrollmentConfirmationFails() {
        Long memberId = 1L;
        Enrollment waitlistedEnrollment = enrollmentRepository.save(
                new Enrollment(memberId, 10L, EnrollmentStatus.WAITLISTED)
        );
        PaymentRequest request = new PaymentRequest(waitlistedEnrollment.getId(), BigDecimal.valueOf(50_000));

        assertThatCode(() -> paymentService.confirmPayment(memberId, request))
                .doesNotThrowAnyException();

        List<Payment> payments = paymentRepository.findAll();
        assertThat(payments).hasSize(1);
        assertThat(payments.get(0).getMemberId()).isEqualTo(memberId);
        assertThat(payments.get(0).getEnrollmentId()).isEqualTo(waitlistedEnrollment.getId());
        assertThat(payments.get(0).getStatus()).isEqualTo(PaymentStatus.SUCCESS);

        Enrollment unchangedEnrollment = enrollmentRepository.findById(waitlistedEnrollment.getId()).orElseThrow();
        assertThat(unchangedEnrollment.getStatus()).isEqualTo(EnrollmentStatus.WAITLISTED);
        assertThat(unchangedEnrollment.getPaymentDate()).isNull();
        assertThat(unchangedEnrollment.getCancelDeadline()).isNull();
    }
}
