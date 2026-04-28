package com.classbooking.notification;

import com.classbooking.common.BaseEntity;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
public class Notification extends BaseEntity {
    private Long memberId;
    private String title;
    private String content;
    private LocalDateTime readAt;

    public Notification(Long memberId, String title, String content) {
        this.memberId = memberId;
        this.title = title;
        this.content = content;
    }
}
