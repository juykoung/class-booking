package com.classbooking.lecture;

import com.classbooking.member.Member;
import com.classbooking.member.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LectureService {
    private final MemberRepository memberRepository;
    private final LectureRepository lectureRepository;

    @Transactional
    public LectureCreResponse createLecture(Long memberId, LectureRequest request) {
        Member instructor = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

        if (!instructor.isInstructor()) {
            throw new IllegalArgumentException("강의 개설은 강사만 가능합니다.");
        }

        Lecture lecture = new Lecture(
                memberId,
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
}
