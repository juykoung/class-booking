package com.classbooking.lecture.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LectureCreRequest(
        @Schema(example = "Spring Boot Basic")
        String title,
        @Schema(example = "REST API and JPA class with Spring Boot.")
        String description,
        @Schema(example = "10")
        int capacity,
        @Schema(example = "1000")
        BigDecimal price,
        @Schema(type = "string", example = "2026-05-10T10:00:00")
        LocalDateTime startAt,
        @Schema(type = "string", example = "2026-05-10T12:00:00")
        LocalDateTime endAt
) {
}
