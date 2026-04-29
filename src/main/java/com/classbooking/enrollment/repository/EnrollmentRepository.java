package com.classbooking.enrollment.repository;

import com.classbooking.enrollment.dto.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    boolean existsByMemberIdAndLectureId(Long memberId, Long lectureId);
}
