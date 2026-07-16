package com.smartlearning.lms.repository;

import com.smartlearning.lms.domain.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    // fetch join: 수강 목록을 DTO로 바꿀 때 course에 접근하므로
    // N+1 쿼리를 막기 위해 한 방에 함께 조회한다 (JPA 수업 복습!)
    @Query("select e from Enrollment e join fetch e.course where e.student.id = :studentId")
    List<Enrollment> findAllByStudentIdWithCourse(@Param("studentId") Long studentId);
}
