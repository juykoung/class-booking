package com.classbooking.enrollment.dto;

import java.time.LocalDateTime;

public record EnrollmentListResponse(
        Long id,
        Long memberId,
        Long lectureId,
        EnrollmentStatus status,
        LocalDateTime paymentDate,
        LocalDateTime cancelDeadline,
        LocalDateTime payDeadline,
        LocalDateTime cancelledAt
) {
    public static EnrollmentListResponse from(Enrollment enrollment) {
        return new EnrollmentListResponse(
                enrollment.getId(),
                enrollment.getMemberId(),
                enrollment.getLectureId(),
                enrollment.getStatus(),
                enrollment.getPaymentDate(),
                enrollment.getCancelDeadline(),
                enrollment.getPayDeadline(),
                enrollment.getCancelledAt()
        );
    }
}
