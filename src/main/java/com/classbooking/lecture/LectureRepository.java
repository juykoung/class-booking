package com.classbooking.lecture;

import com.classbooking.lecture.dto.Lecture;
import com.classbooking.lecture.dto.LectureStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LectureRepository extends JpaRepository<Lecture, Long> {

    Page<Lecture> findByStatus(LectureStatus status, Pageable pageable);
}
