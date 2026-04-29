package com.classbooking.lecture.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LectureListResponse(
        Long id, Long instructorId, String instructorName, String title, String description,
        BigDecimal price, LectureStatus status, LocalDateTime startAt, LocalDateTime endAt
) {
    public static LectureListResponse from(Lecture lecture) {
        return new LectureListResponse(
                lecture.getId(),
                lecture.getInstructorId(),
                lecture.getInstructorName(),
                lecture.getTitle(),
                lecture.getDescription(),
                lecture.getPrice(),
                lecture.getStatus(),
                lecture.getStartAt(),
                lecture.getEndAt()
        );
    }
}
