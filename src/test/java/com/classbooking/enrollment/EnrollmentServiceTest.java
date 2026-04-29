package com.classbooking.enrollment;

import com.classbooking.enrollment.dto.Enrollment;
import com.classbooking.enrollment.dto.EnrollmentStatus;
import com.classbooking.enrollment.repository.EnrollmentRepository;
import com.classbooking.enrollment.service.EnrollmentService;
import com.classbooking.lecture.dto.Lecture;
import com.classbooking.lecture.dto.LectureStatus;
import com.classbooking.lecture.repository.LectureRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private LectureRepository lectureRepository;

    @InjectMocks
    private EnrollmentService enrollmentService;

    @Test
    @DisplayName("enroll uses pessimistic lock and increments confirmed count")
    void enrollWithPessimisticLock() {
        Long memberId = 2L;
        Long lectureId = 10L;
        Lecture lecture = openLecture(1L, 2);

        when(enrollmentRepository.existsByMemberIdAndLectureId(memberId, lectureId)).thenReturn(false);
        when(lectureRepository.findByIdWithPessimisticLock(lectureId)).thenReturn(Optional.of(lecture));

        enrollmentService.enroll(memberId, lectureId);

        assertThat(lecture.getEnrolledCount()).isEqualTo(1);
        verify(lectureRepository).findByIdWithPessimisticLock(lectureId);

        ArgumentCaptor<Enrollment> enrollmentCaptor = ArgumentCaptor.forClass(Enrollment.class);
        verify(enrollmentRepository).save(enrollmentCaptor.capture());

        Enrollment savedEnrollment = enrollmentCaptor.getValue();
        assertThat(savedEnrollment.getMemberId()).isEqualTo(memberId);
        assertThat(savedEnrollment.getLectureId()).isEqualTo(lectureId);
        assertThat(savedEnrollment.getStatus()).isEqualTo(EnrollmentStatus.PENDING);
        assertThat(savedEnrollment.getPayDeadline()).isNotNull();
    }

    @Test
    @DisplayName("duplicate enrollment fails after acquiring lecture lock")
    void enrollDuplicateThrowsException() {
        Long memberId = 2L;
        Long lectureId = 10L;
        Lecture lecture = openLecture(1L, 2);

        when(lectureRepository.findByIdWithPessimisticLock(lectureId)).thenReturn(Optional.of(lecture));
        when(enrollmentRepository.existsByMemberIdAndLectureId(memberId, lectureId)).thenReturn(true);

        assertThatThrownBy(() -> enrollmentService.enroll(memberId, lectureId))
                .isInstanceOf(IllegalStateException.class);

        verify(lectureRepository).findByIdWithPessimisticLock(lectureId);
        verify(enrollmentRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("enroll fails when lecture is not open")
    void enrollClosedLectureThrowsException() {
        Long memberId = 2L;
        Long lectureId = 10L;
        Lecture lecture = lecture(1L, 2);

        when(enrollmentRepository.existsByMemberIdAndLectureId(memberId, lectureId)).thenReturn(false);
        when(lectureRepository.findByIdWithPessimisticLock(lectureId)).thenReturn(Optional.of(lecture));

        assertThatThrownBy(() -> enrollmentService.enroll(memberId, lectureId))
                .isInstanceOf(IllegalStateException.class);

        assertThat(lecture.getStatus()).isEqualTo(LectureStatus.DRAFT);
        assertThat(lecture.getEnrolledCount()).isZero();
        verify(enrollmentRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("enroll adds waitlisted enrollment when capacity is full")
    void enrollFullLectureAddsWaitlistedEnrollment() {
        Long memberId = 2L;
        Long lectureId = 10L;
        Lecture lecture = openLecture(1L, 1);
        lecture.enroll();

        when(enrollmentRepository.existsByMemberIdAndLectureId(memberId, lectureId)).thenReturn(false);
        when(lectureRepository.findByIdWithPessimisticLock(lectureId)).thenReturn(Optional.of(lecture));

        enrollmentService.enroll(memberId, lectureId);

        assertThat(lecture.getEnrolledCount()).isEqualTo(1);

        ArgumentCaptor<Enrollment> enrollmentCaptor = ArgumentCaptor.forClass(Enrollment.class);
        verify(enrollmentRepository).save(enrollmentCaptor.capture());

        Enrollment savedEnrollment = enrollmentCaptor.getValue();
        assertThat(savedEnrollment.getMemberId()).isEqualTo(memberId);
        assertThat(savedEnrollment.getLectureId()).isEqualTo(lectureId);
        assertThat(savedEnrollment.getStatus()).isEqualTo(EnrollmentStatus.WAITLISTED);
        assertThat(savedEnrollment.getPayDeadline()).isNull();
    }

    @Test
    @DisplayName("enroll fails when lecture does not exist")
    void enrollUnknownLectureThrowsException() {
        Long memberId = 2L;
        Long lectureId = 10L;

        when(lectureRepository.findByIdWithPessimisticLock(lectureId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> enrollmentService.enroll(memberId, lectureId))
                .isInstanceOf(IllegalArgumentException.class);

        verify(enrollmentRepository, never()).existsByMemberIdAndLectureId(memberId, lectureId);
        verify(enrollmentRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    private Lecture openLecture(Long instructorId, int capacity) {
        Lecture lecture = lecture(instructorId, capacity);
        lecture.open(instructorId);
        return lecture;
    }

    private Lecture lecture(Long instructorId, int capacity) {
        return new Lecture(
                instructorId,
                "Instructor",
                "Spring Boot",
                "Spring Boot basics",
                capacity,
                BigDecimal.valueOf(50_000),
                LocalDateTime.of(2026, 5, 1, 10, 0),
                LocalDateTime.of(2026, 6, 1, 10, 0)
        );
    }
}
