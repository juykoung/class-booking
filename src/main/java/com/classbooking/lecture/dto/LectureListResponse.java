package com.classbooking.lecture.dto;

import java.math.BigDecimal;

public record LectureListResponse(
        Long id, Long instructorId, String title, String description, BigDecimal price, LectureStatus status
) {
    public static LectureListResponse from(Lecture lecture) {
        return new LectureListResponse(
                lecture.getId(),
                lecture.getInstructorId(),
                lecture.getTitle(),
                lecture.getDescription(),
                lecture.getPrice(),
                lecture.getStatus()
        );
    }
}
