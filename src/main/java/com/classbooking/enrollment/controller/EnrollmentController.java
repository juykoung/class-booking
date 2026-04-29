package com.classbooking.enrollment.controller;

import com.classbooking.enrollment.dto.EnrollmentListResponse;
import com.classbooking.enrollment.dto.EnrollmentStatus;
import com.classbooking.enrollment.dto.LectureEnrollmentResponse;
import com.classbooking.enrollment.service.EnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/enrollment")
@RequiredArgsConstructor
@Tag(name = "Enrollment", description = "수강 신청, 취소, 수강 신청 목록 API")
public class EnrollmentController {
    private final EnrollmentService enrollmentService;

    @Operation(summary = "수강 신청", description = "강의에 수강 신청합니다. 정원 내 신청은 PENDING, 정원 초과 신청은 WAITLISTED로 저장됩니다.")
    @PostMapping("/{lectureId}/enroll")
    public ResponseEntity<Void> enroll(
            @Parameter(description = "수강생 회원 ID", example = "2")
            @RequestHeader("X-Member-Id") Long memberId,
            @Parameter(description = "강의 ID", example = "1")
            @PathVariable Long lectureId
    ) {
        enrollmentService.enroll(memberId, lectureId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "수강 취소", description = "확정된 수강 신청을 취소합니다. 취소 후 결제 환불 이벤트가 발행됩니다.")
    @PostMapping("/{enrollmentId}/withdraw")
    public ResponseEntity<Void> withdraw(
            @Parameter(description = "수강생 회원 ID", example = "2")
            @RequestHeader("X-Member-Id") Long memberId,
            @Parameter(description = "수강 신청 ID", example = "10")
            @PathVariable Long enrollmentId
    ) {
        enrollmentService.withdraw(memberId, enrollmentId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "내 수강 신청 목록 조회", description = "요청 회원의 수강 신청 목록을 조회합니다. 상태 필터는 선택값입니다.")
    @GetMapping("/list")
    public ResponseEntity<Page<EnrollmentListResponse>> getMyEnrollments(
            @Parameter(description = "요청 회원 ID", example = "2")
            @RequestHeader("X-Member-Id") Long memberId,
            @Parameter(description = "수강 신청 상태 필터", example = "CONFIRMED")
            @RequestParam(required = false) EnrollmentStatus status,
            @Parameter(description = "페이지 번호", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10")
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<EnrollmentListResponse> response = enrollmentService.getMyEnrollments(memberId, status, page, size);
        return ResponseEntity.ok(response);
    }
}
