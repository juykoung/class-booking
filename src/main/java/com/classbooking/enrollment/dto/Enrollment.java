package com.classbooking.enrollment.dto;

import com.classbooking.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "enrollments", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"memberId", "lectureId"})
})
public class Enrollment extends BaseEntity {

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private Long lectureId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EnrollmentStatus status;

    @Column(nullable = true)
    private LocalDateTime paymentDate;           // 결제 & 수강 확정 일시

    @Column(nullable = true)
    private LocalDateTime cancelDeadline;       // 수강 취소 가능 마감일시 (결제 후 7일)

    @Column(nullable = true)
    private LocalDateTime payDeadline;          // 결제 마감일시 (수강 신청 후 3일)

    @Column(nullable = true)
    private LocalDateTime cancelledAt;          // 수강 취소 일시

    public Enrollment(Long memberId, Long lectureId) {
        this(memberId, lectureId, EnrollmentStatus.PENDING);
    }

    public Enrollment(Long memberId, Long lectureId, EnrollmentStatus status) {
        this.memberId = memberId;
        this.lectureId = lectureId;
        this.status = status;
        this.paymentDate = null;
        this.cancelDeadline = null;
        this.payDeadline = status == EnrollmentStatus.PENDING ? LocalDateTime.now().plusDays(3) : null;
        this.cancelledAt = null;
    }

    public void validatePayable(Long memberId) {
        if (!this.memberId.equals(memberId)) {
            throw new IllegalArgumentException("해당 수강의 주인만 결제할 수 있습니다.");
        }
        validateConfirmable();
    }

    // 결제&수강 확정 처리
    public void confirmPayment() {
        validateConfirmable();
        this.status = EnrollmentStatus.CONFIRMED;
        this.paymentDate = LocalDateTime.now();
        this.cancelDeadline = this.paymentDate.plusDays(7);
    }

    private void validateConfirmable() {
        if (this.payDeadline != null && LocalDateTime.now().isAfter(this.payDeadline)) {
            throw new IllegalStateException("결제 마감일이 지났습니다.");
        }

        if (this.status != EnrollmentStatus.PENDING) {
            throw new IllegalStateException("결제 확정은 PENDING 상태에서만 가능합니다.");
        }
    }

    // 수강 취소
    public void withdraw(Long memberId) {
        if (!this.memberId.equals(memberId)) {
            throw new IllegalArgumentException("수강 취소는 본인만 가능합니다.");
        }

        if (this.status != EnrollmentStatus.CONFIRMED) {
            throw new IllegalStateException("수강 취소는 CONFIRMED 상태에서만 가능합니다.");
        }
        if (LocalDateTime.now().isAfter(this.cancelDeadline)) {
            throw new IllegalStateException("수강 취소 마감일이 지났습니다.");
        }
        this.status = EnrollmentStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
    }
}
