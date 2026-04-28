package com.classbooking.member;

import com.classbooking.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class Member extends BaseEntity {

    @Column(nullable = false)
    private String memberId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MemberRole role;

    private Member(String memberId, String name, String nickname, String email, String password, MemberRole role) {
        this.memberId = memberId;
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public boolean isInstructor() {
        return this.role == MemberRole.INSTRUCTOR;
    }
}
