package com.classbooking.lecture.repository;

import com.classbooking.lecture.dto.Lecture;
import com.classbooking.lecture.dto.LectureStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LectureRepository extends JpaRepository<Lecture, Long> {

    Page<Lecture> findByStatus(LectureStatus status, Pageable pageable);

    Optional<Lecture> findByIdAndInstructorId(Long id, Long instructorId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select l from Lecture l where l.id = :id")
    Optional<Lecture> findByIdWithPessimisticLock(@Param("id") Long id);
}
