package com.classbooking.enrollment.dto;

public enum EnrollmentStatus {
    PENDING,     // 신청 완료, 결제 대기
    CONFIRMED,   // 수강 확정, 결제 완료
    CANCELLED,   // 신청 취소
    WAITLISTED   // 대기자 명단에 등록된 상태
}
