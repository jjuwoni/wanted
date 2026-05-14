package com.wanted.springevent.enrollment.repository;

import com.wanted.springevent.enrollment.entity.Enrollment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    // EntityGraph 는 연관관계를 통한 엔티티 탐색을 하는 것이다.
    // 지금은 enrollmentId 를 기준으로 user 와 course 데이터를 탐색할 수 있다.
    @EntityGraph(attributePaths = {"user", "course"})
    Optional<Enrollment> findWithUserAndCourseByEnrollmentId(Long enrollmentId);
}
