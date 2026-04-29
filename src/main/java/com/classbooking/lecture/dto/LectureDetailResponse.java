package com.classbooking.lecture.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LectureDetailResponse(
        Long id, Long instructorId, String instructorName, String title, String description,
        BigDecimal price, Integer capacity, Integer confirmedCount, LectureStatus status,
        LocalDateTime startAt, LocalDateTime endAt
) {
    public static LectureDetailResponse from(Lecture lecture) {
        return new LectureDetailResponse(
                lecture.getId(),
                lecture.getInstructorId(),
                lecture.getInstructorName(),
                lecture.getTitle(),
                lecture.getDescription(),
                lecture.getPrice(),
                lecture.getCapacity(),
                lecture.getConfirmedCount(),
                lecture.getStatus(),
                lecture.getStartAt(),
                lecture.getEndAt()
        );
    }
}
