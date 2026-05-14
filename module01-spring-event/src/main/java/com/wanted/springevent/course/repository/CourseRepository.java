package com.wanted.springevent.course.repository;

import com.wanted.springevent.course.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
}
