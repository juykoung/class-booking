package com.classbooking.enrollment.service;

import com.classbooking.enrollment.dto.Enrollment;
import com.classbooking.enrollment.dto.EnrollmentStatus;
import com.classbooking.enrollment.repository.EnrollmentRepository;
import com.classbooking.lecture.dto.Lecture;
import com.classbooking.lecture.repository.LectureRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final LectureRepository lectureRepository;

    @Transactional
    public void enroll(Long memberId, Long lectureId) {
        Lecture lecture = lectureRepository.findByIdWithPessimisticLock(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다."));

        if (enrollmentRepository.existsByMemberIdAndLectureId(memberId, lectureId)) {
            throw new IllegalStateException("이미 신청한 강의입니다.");
        }

        boolean seatReserved = lecture.enroll();
        EnrollmentStatus status = seatReserved ? EnrollmentStatus.PENDING : EnrollmentStatus.WAITLISTED;

        Enrollment enrollment = new Enrollment(memberId, lectureId, status);
        enrollmentRepository.save(enrollment);
    }

    // 결제 확정 이벤트 수신 후 enrollment 상태 업데이트
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void confirmPayment(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException("수강 신청을 찾을 수 없습니다."));

        enrollment.confirmPayment();
    }

    // 수강 취소
    // 내 수강 신청 목록 조회
}
