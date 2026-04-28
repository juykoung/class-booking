package com.classbooking.enrollment;

import com.classbooking.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
public class Enrollment extends BaseEntity {

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private Long lectureId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EnrollmentStatus status;

    @Column(nullable = true)
    private LocalDateTime paymentDate;           // 결제일시

    @Column(nullable = true)
    private LocalDateTime confirmationDate;     // 수강 확정일시

    @Column(nullable = true)
    private LocalDateTime cancelDeadline;       // 수강 취소 가능 마감일시 (결제 후 7일)

    @Column(nullable = false)
    private LocalDateTime payDeadline;          // 결제 마감일시 (수강 신청 후 3일)

    @Column(nullable = true)
    private LocalDateTime cancelledAt;          // 수강 취소 일시

    public Enrollment(Long memberId, Long lectureId, LocalDateTime confirmationDate, LocalDateTime cancelDeadline, LocalDateTime payDeadline) {
        this.memberId = memberId;
        this.lectureId = lectureId;
        this.status = EnrollmentStatus.PENDING; // 초기 상태는 PENDING
        this.paymentDate = null;
        this.confirmationDate = confirmationDate;
        this.cancelDeadline = cancelDeadline;
        this.payDeadline = LocalDateTime.now().plusDays(3);
        this.cancelledAt = null;
    }

    // 결제 확정 처리

    // 수강 취소
}
