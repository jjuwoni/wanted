package com.smartlearning.lms.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 조회 전용 스켈레톤이므로 지연 로딩 + 단방향으로 단순하게 유지한다
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    private int progressRate;   // 진도율 0~100 (%)
    private Integer score;      // 성적 (아직 없으면 null → Integer 사용)
    private String status;      // IN_PROGRESS / COMPLETED
}
