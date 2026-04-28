package com.classbooking.lecture;

import com.classbooking.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
public class Lecture extends BaseEntity {
    @Column(nullable = false)
    private Long instructorId;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = true)
    private Integer confirmedCount;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private LocalDateTime startAt;

    @Column(nullable = false)
    private LocalDateTime endAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LectureStatus status;

    public Lecture(Long instructorId, String title, String description, Integer capacity, BigDecimal price, LocalDateTime startTime, LocalDateTime endTime) {
        this.instructorId = instructorId;
        this.title = title;
        this.description = description;
        this.capacity = capacity;
        this.confirmedCount = 0; // 초기값은 0
        this.price = price;
        this.startAt = startTime;
        this.endAt = endTime;
        this.status = LectureStatus.DRAFT; // 초기 상태는 DRAFT
    }
}