package com.classbooking.payment.repository;

import com.classbooking.payment.dto.Payment;
import com.classbooking.payment.dto.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByEnrollmentIdAndStatus(Long enrollmentId, PaymentStatus status);
}
