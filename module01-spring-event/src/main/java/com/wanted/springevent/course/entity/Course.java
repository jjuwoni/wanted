package com.wanted.springevent.course.entity;

import com.wanted.springevent.user.entity.User;
import jakarta.persistence.*;

@Entity
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Long courseId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private CourseStatus status;

    protected Course() {
    }

    private Course(User author, String title, String description, CourseStatus status) {
        this.author = author;
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public static Course create(User author, String title, String description, CourseStatus status) {
        return new Course(author, title, description, status);
    }

    public Long getCourseId() {
        return courseId;
    }

    public String getTitle() {
        return title;
    }
}
