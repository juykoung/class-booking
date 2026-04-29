package com.classbooking.payment;

import com.classbooking.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
public class Payment extends BaseEntity {

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private Long enrollmentId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column(nullable = false)
    private LocalDateTime paymentAt;

    @Column(nullable = true)
    private LocalDateTime refundedAt;

    public Payment(Long memberId, Long enrollmentId, BigDecimal amount, PaymentStatus status) {
        this.memberId = memberId;
        this.enrollmentId = enrollmentId;
        this.amount = amount;
        this.status = status;
        this.paymentAt = LocalDateTime.now();
        this.refundedAt = null;
    }

    public void refund() {
        if (this.status != PaymentStatus.SUCCESS) {
            throw new IllegalStateException("환불은 성공한 결제에 대해서만 가능합니다.");
        }

        this.status = PaymentStatus.REFUNDED;
        this.refundedAt = LocalDateTime.now();
    }
}
