package com.classbooking.payment;

import com.classbooking.enrollment.dto.Enrollment;
import com.classbooking.enrollment.dto.EnrollmentStatus;
import com.classbooking.enrollment.repository.EnrollmentRepository;
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

    @BeforeEach
    void setUp() {
        paymentRepository.deleteAll();
        enrollmentRepository.deleteAll();
    }

    @Test
    @DisplayName("payment success confirms pending enrollment")
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
    @DisplayName("payment fails before saving when enrollment is not payable")
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
    @DisplayName("payment fails before saving when pay deadline has passed")
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
}
