package com.classbooking.lecture.service;

import com.classbooking.lecture.repository.LectureRepository;
import com.classbooking.lecture.dto.*;
import com.classbooking.member.Member;
import com.classbooking.member.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LectureService {
    private final MemberRepository memberRepository;
    private final LectureRepository lectureRepository;

    @Transactional
    public LectureCreResponse createLecture(Long memberId, LectureCreRequest request) {
        Member instructor = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

        if (!instructor.isInstructor()) {
            throw new IllegalArgumentException("강의 개설은 강사만 가능합니다.");
        }

        Lecture lecture = new Lecture(
                memberId,
                instructor.getName(),
                request.title(),
                request.description(),
                request.capacity(),
                request.price(),
                request.startAt(),
                request.endAt()
        );

        Lecture savedLecture = lectureRepository.save(lecture);

        return new LectureCreResponse(savedLecture.getId(), savedLecture.getInstructorId(), savedLecture.getTitle());
    }

    @Transactional(readOnly = true)
    public Page<LectureListResponse> getLectures(LectureStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Lecture> lectures = status == null
                ? lectureRepository.findAll(pageable)
                : lectureRepository.findByStatus(status, pageable);

        return lectures.map(LectureListResponse::from);
    }

    @Transactional(readOnly = true)
    public LectureDetailResponse getLectureDetail(Long memberId, Long lectureId) {
        Lecture lecture = lectureRepository.findByIdAndInstructorId(lectureId, memberId)
                .orElseThrow(() -> new EntityNotFoundException("강의를 찾을 수 없습니다."));

        return LectureDetailResponse.from(lecture);

    }

    @Transactional
    public void openLecture(Long memberId, Long lectureId) {
       Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new EntityNotFoundException("강의를 찾을 수 없습니다."));

        lecture.open(memberId);
    }

    @Transactional
    public void closeLecture(Long memberId, Long lectureId) {
      Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new EntityNotFoundException("강의를 찾을 수 없습니다."));

        lecture.close(memberId);
    }
}
