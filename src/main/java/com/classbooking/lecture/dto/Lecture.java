package com.classbooking.lecture.dto;

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

    @Column(nullable = false, length = 10)
    private String instructorName;

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

    public Lecture(Long instructorId, String instructorName, String title, String description, Integer capacity, BigDecimal price, LocalDateTime startAt, LocalDateTime endAt) {
        this.instructorId = instructorId;
        this.instructorName = instructorName;
        this.title = title;
        this.description = description;
        this.capacity = capacity;
        this.confirmedCount = 0; // 초기값은 0
        this.price = price;
        this.startAt = startAt;
        this.endAt = endAt;
        this.status = LectureStatus.DRAFT; // 초기 상태는 DRAFT
    }

    public void open(Long memberId) {
        validateOwner(memberId);

        if (this.status != LectureStatus.DRAFT) {
            throw new IllegalStateException("강의는 DRAFT 상태에서만 OPEN으로 변경할 수 있습니다.");
        }
        this.status = LectureStatus.OPEN;
    }

    public void close(Long memberId) {
        validateOwner(memberId);

        if (this.status != LectureStatus.OPEN) {
            throw new IllegalStateException("강의는 OPEN 상태에서만 CLOSE로 변경할 수 있습니다.");
        }
        this.status = LectureStatus.CLOSED;
    }

    public void validateOwner(Long memberId) {
        if (!this.instructorId.equals(memberId)) {
            throw new IllegalArgumentException("강의 수정은 강사 본인만 가능합니다.");
        }
    }
}