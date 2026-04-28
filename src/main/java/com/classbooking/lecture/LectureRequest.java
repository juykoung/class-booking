package com.classbooking.lecture;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LectureRequest(
        String title,
        String description,
        Integer capacity,
        BigDecimal price,
        LocalDateTime startAt,
        LocalDateTime endAt
) {
}
