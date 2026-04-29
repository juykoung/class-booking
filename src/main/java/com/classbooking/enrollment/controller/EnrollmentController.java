package com.classbooking.enrollment.controller;

import com.classbooking.enrollment.dto.EnrollmentListResponse;
import com.classbooking.enrollment.dto.EnrollmentStatus;
import com.classbooking.enrollment.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/enrollment")
@RequiredArgsConstructor
public class EnrollmentController {
    private final EnrollmentService enrollmentService;

    // 수강 신청
    @PostMapping("/{lectureId}/enroll")
    public ResponseEntity<Void> enroll(
            @RequestHeader("X-Member-Id") Long memberId,
            @PathVariable Long lectureId) {
        enrollmentService.enroll(memberId, lectureId);
        return ResponseEntity.ok().build();
    }

    // 수강 취소
    @PostMapping("/{enrollmentId}/withdraw")
    public ResponseEntity<Void> withdraw(
            @RequestHeader("X-Member-Id") Long memberId,
            @PathVariable Long enrollmentId) {
        enrollmentService.withdraw(memberId, enrollmentId);
        return ResponseEntity.ok().build();
    }

    // 내 수강 신청 목록 조회
    @GetMapping("/list")
    public ResponseEntity<Page<EnrollmentListResponse>> getMyEnrollments(
            @RequestHeader("X-Member-Id") Long memberId,
            @RequestParam(required = false) EnrollmentStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<EnrollmentListResponse> response = enrollmentService.getMyEnrollments(memberId, status, page, size);
        return ResponseEntity.ok(response);
    }
}
