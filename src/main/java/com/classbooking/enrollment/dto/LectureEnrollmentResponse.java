package com.classbooking.enrollment.dto;

import com.classbooking.member.Member;

public record LectureEnrollmentResponse(
        Long memberId,
        String name,
        EnrollmentStatus status
) {
    public static LectureEnrollmentResponse of(Enrollment enrollment, Member member) {
        return new LectureEnrollmentResponse(
                enrollment.getMemberId(),
                member.getName(),
                enrollment.getStatus()
        );
    }
}
