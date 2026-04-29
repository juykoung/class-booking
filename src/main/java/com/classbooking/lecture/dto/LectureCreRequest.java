package com.classbooking.lecture.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LectureCreRequest(
        String title,
        String description,
        Integer capacity,
        BigDecimal price,
        LocalDateTime startAt,
        LocalDateTime endAt
) {
}
