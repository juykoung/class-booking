package com.classbooking.enrollment.repository;

import com.classbooking.enrollment.dto.Enrollment;
import com.classbooking.enrollment.dto.EnrollmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    boolean existsByMemberIdAndLectureId(Long memberId, Long lectureId);

    Page<Enrollment> findByMemberId(Long memberId, Pageable pageable);

    Page<Enrollment> findByMemberIdAndStatus(Long memberId, EnrollmentStatus status, Pageable pageable);

    Page<Enrollment> findByLectureId(Long lectureId, Pageable pageable);

    Page<Enrollment> findByLectureIdAndStatus(Long lectureId, EnrollmentStatus status, Pageable pageable);
}
