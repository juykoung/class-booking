package com.classbooking.enrollment.service;

import com.classbooking.enrollment.dto.Enrollment;
import com.classbooking.enrollment.dto.EnrollmentListResponse;
import com.classbooking.enrollment.dto.EnrollmentStatus;
import com.classbooking.enrollment.dto.EnrollmentWithdrawnEvent;
import com.classbooking.enrollment.dto.LectureEnrollmentResponse;
import com.classbooking.enrollment.repository.EnrollmentRepository;
import com.classbooking.lecture.dto.Lecture;
import com.classbooking.lecture.repository.LectureRepository;
import com.classbooking.member.Member;
import com.classbooking.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final LectureRepository lectureRepository;
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;

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

    @Transactional
    public void withdraw(Long memberId, Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException("수강 신청을 찾을 수 없습니다."));

        enrollment.withdraw(memberId);

        Lecture lecture = lectureRepository.findByIdWithPessimisticLock(enrollment.getLectureId())
                .orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다."));
        lecture.withdraw();

        eventPublisher.publishEvent(new EnrollmentWithdrawnEvent(enrollmentId));
    }

    @Transactional(readOnly = true)
    public Page<EnrollmentListResponse> getMyEnrollments(Long memberId, EnrollmentStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Enrollment> enrollments = status == null
                ? enrollmentRepository.findByMemberId(memberId, pageable)
                : enrollmentRepository.findByMemberIdAndStatus(memberId, status, pageable);

        return enrollments.map(EnrollmentListResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<LectureEnrollmentResponse> getLectureEnrollments(
            Long instructorId, Long lectureId, EnrollmentStatus status, int page, int size
    ) {
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다."));

        lecture.validateOwner(instructorId);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Enrollment> enrollments = status == null ?
                enrollmentRepository.findByLectureId(lectureId, pageable) :
                enrollmentRepository.findByLectureIdAndStatus(lectureId, status, pageable);

        Map<Long, Member> membersById = memberRepository.findAllById( enrollments.getContent().stream()
                .map(Enrollment::getMemberId)
                .toList() ).stream()
                .collect(Collectors.toMap(Member::getId, Function.identity()));
        return enrollments.map(enrollment -> {
            Member member = membersById.get(enrollment.getMemberId());
            if (member == null) {
                throw new IllegalStateException("회원을 찾을 수 없습니다.");
            }
            return LectureEnrollmentResponse.of(enrollment, member);
        });
    }
}
