package com.classbooking.enrollment.controller;

import com.classbooking.enrollment.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/enrollment")
@RequiredArgsConstructor
public class EnrollmentController {
    private final EnrollmentService enrollmentService;

    @PostMapping("/{lectureId}/enroll")
    public ResponseEntity<Void> enroll(
            @RequestHeader("X-Member-Id") Long memberId,
            @PathVariable Long lectureId) {
        enrollmentService.enroll(memberId, lectureId);
        return ResponseEntity.ok().build();
    }

    // 수강 취소
    // 내 수강 신청 목록 조회
}
