package com.wanted.springevent.enrollment.entity;

import com.wanted.springevent.course.entity.Course;
import com.wanted.springevent.user.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "enrollments")
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "enrollment_id")
    private Long enrollmentId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private EnrollmentStatus status;

    @Column(name = "enrolled_at", nullable = false, updatable = false, insertable = false)
    private LocalDateTime enrolledAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    protected Enrollment() {
    }

    private Enrollment(User user, Course course, EnrollmentStatus status) {
        this.user = user;
        this.course = course;
        this.status = status;
    }

    public static Enrollment create(User user, Course course) {
        return new Enrollment(user, course, EnrollmentStatus.active);
    }

    public void complete() {
        if (this.status == EnrollmentStatus.completed) {
            throw new IllegalStateException("이미 수료 처리된 수강 정보입니다. enrollmentId=" + enrollmentId);
        }
        this.status = EnrollmentStatus.completed;
        this.completedAt = LocalDateTime.now();
    }

    public Long getEnrollmentId() {
        return enrollmentId;
    }

    public User getUser() {
        return user;
    }

    public Course getCourse() {
        return course;
    }
}
