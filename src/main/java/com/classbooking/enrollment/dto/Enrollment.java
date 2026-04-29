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

    // 결제 확정 처리

    // 수강 취소
}
