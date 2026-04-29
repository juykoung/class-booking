package com.classbooking.lecture;

import com.classbooking.lecture.dto.Lecture;
import com.classbooking.lecture.dto.LectureCreResponse;
import com.classbooking.lecture.dto.LectureCreRequest;
import com.classbooking.lecture.dto.LectureDetailResponse;
import com.classbooking.lecture.dto.LectureStatus;
import com.classbooking.member.Member;
import com.classbooking.member.MemberRepository;
import com.classbooking.member.MemberRole;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LectureServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private LectureRepository lectureRepository;

    @InjectMocks
    private LectureService lectureService;

    @Test
    @DisplayName("강사는 강의를 개설할 수 있다")
    void createLectureByInstructor() {
        LectureCreRequest request = lectureRequest();
        Member instructor = member(MemberRole.INSTRUCTOR);

        when(memberRepository.findById(instructor.getId())).thenReturn(Optional.of(instructor));
        when(lectureRepository.save(any(Lecture.class))).thenAnswer(invocation -> {
            Lecture lecture = invocation.getArgument(0);
            ReflectionTestUtils.setField(lecture, "id", 1L);
            return lecture;
        });

        LectureCreResponse response = lectureService.createLecture(instructor.getId(), request);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.instructorId()).isEqualTo(instructor.getId());
        assertThat(response.title()).isEqualTo(request.title());

        ArgumentCaptor<Lecture> lectureCaptor = ArgumentCaptor.forClass(Lecture.class);
        verify(lectureRepository).save(lectureCaptor.capture());

        Lecture savedLecture = lectureCaptor.getValue();
        assertThat(savedLecture.getInstructorId()).isEqualTo(instructor.getId());
        assertThat(savedLecture.getTitle()).isEqualTo(request.title());
        assertThat(savedLecture.getDescription()).isEqualTo(request.description());
        assertThat(savedLecture.getCapacity()).isEqualTo(request.capacity());
        assertThat(savedLecture.getPrice()).isEqualByComparingTo(request.price());
        assertThat(savedLecture.getStartAt()).isEqualTo(request.startAt());
        assertThat(savedLecture.getEndAt()).isEqualTo(request.endAt());
        assertThat(savedLecture.getStatus()).isEqualTo(LectureStatus.DRAFT);
    }

    @Test
    @DisplayName("강사가 아닌 회원은 강의를 개설할 수 없다")
    void createLectureByNonInstructorThrowsException() {
        LectureCreRequest request = lectureRequest();
        Member student = member(MemberRole.STUDENT);

        when(memberRepository.findById(student.getId())).thenReturn(Optional.of(student));


        assertThatThrownBy(() -> lectureService.createLecture(student.getId(), request))
                .isInstanceOf(IllegalArgumentException.class);

        verify(lectureRepository, never()).save(any());
    }

    @Test
    @DisplayName("존재하지 않는 회원이면 강의를 개설할 수 없다")
    void createLectureByUnknownMemberThrowsException() {
        LectureCreRequest request = lectureRequest();
        Long unknownMemberId = 99L;

        when(memberRepository.findById(unknownMemberId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> lectureService.createLecture(unknownMemberId, request))
                .isInstanceOf(EntityNotFoundException.class);

        verify(lectureRepository, never()).save(any());
    }

    @Test
    @DisplayName("lecture detail does not check instructor role")
    void getLectureDetailDoesNotCheckInstructorRole() {
        Member student = member(1L, MemberRole.STUDENT);
        Lecture lecture = lecture(student.getId());
        ReflectionTestUtils.setField(lecture, "id", 10L);

        when(lectureRepository.findByIdAndInstructorId(lecture.getId(), student.getId()))
                .thenReturn(Optional.of(lecture));

        LectureDetailResponse response = lectureService.getLectureDetail(student.getId(), lecture.getId());

        assertThat(response.id()).isEqualTo(lecture.getId());
        assertThat(response.instructorId()).isEqualTo(student.getId());
        verify(memberRepository, never()).findById(any());
    }

    @Test
    @DisplayName("lecture list does not check instructor role")
    void getLecturesDoesNotCheckInstructorRole() {
        Lecture lecture = lecture(1L);

        when(lectureRepository.findAll(any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(lecture)));

        Page<?> response = lectureService.getLectures(null, 0, 10);

        assertThat(response.getContent()).hasSize(1);
        verify(memberRepository, never()).findById(any());
    }

    private LectureCreRequest lectureRequest() {
        return new LectureCreRequest(
                "Spring Boot",
                "Spring Boot basics",
                30,
                BigDecimal.valueOf(50_000),
                LocalDateTime.of(2026, 5, 1, 10, 0),
                LocalDateTime.of(2026, 6, 1, 10, 0)
        );
    }

    private Member member(MemberRole role) {
        return member(1L, role);
    }

    private Member member(Long id, MemberRole role) {
        Member member = new Member();
        ReflectionTestUtils.setField(member, "id", id);
        ReflectionTestUtils.setField(member, "role", role);
        return member;
    }

    private Lecture lecture(Long instructorId) {
        return new Lecture(
                instructorId,
                "Instructor",
                "Spring Boot",
                "Spring Boot basics",
                30,
                BigDecimal.valueOf(50_000),
                LocalDateTime.of(2026, 5, 1, 10, 0),
                LocalDateTime.of(2026, 6, 1, 10, 0)
        );
    }
}
