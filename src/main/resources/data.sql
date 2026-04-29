INSERT INTO members (
    id,
    member_id,
    name,
    nickname,
    email,
    password,
    role,
    created_at,
    updated_at
) VALUES
    (1, 'instructor01', '김강사', 'teacher', 'teacher@example.com', 'password', 'INSTRUCTOR', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 'student01', '이학생', 'student1', 'student1@example.com', 'password', 'STUDENT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (3, 'student02', '박학생', 'student2', 'student2@example.com', 'password', 'STUDENT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO lectures (
    id,
    instructor_id,
    instructor_name,
    title,
    description,
    capacity,
    enrolled_count,
    price,
    start_at,
    end_at,
    status,
    created_at,
    updated_at
) VALUES
    (
        1,
        1,
        '김강사',
        'Spring Boot 입문',
        'Spring Boot 기반으로 REST API와 JPA를 학습하는 강의입니다.',
        2,
        0,
        50000,
        TIMESTAMP '2026-05-10 10:00:00',
        TIMESTAMP '2026-05-10 12:00:00',
        'OPEN',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        2,
        1,
        '김강사',
        'JPA 실전',
        '연관관계, 트랜잭션, 락을 실습하는 강의입니다.',
        1,
        0,
        70000,
        TIMESTAMP '2026-05-12 14:00:00',
        TIMESTAMP '2026-05-12 16:00:00',
        'DRAFT',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    );

ALTER TABLE members ALTER COLUMN id RESTART WITH 4;
ALTER TABLE lectures ALTER COLUMN id RESTART WITH 3;
