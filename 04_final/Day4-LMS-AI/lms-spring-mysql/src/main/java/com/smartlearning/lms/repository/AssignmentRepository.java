package com.smartlearning.lms.repository;

import com.smartlearning.lms.domain.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    // "학생이 수강 중인 강좌"의 과제 중, 오늘~기준일 사이 마감분을 조회
    @Query("""
            select a from Assignment a join fetch a.course c
            where c.id in (select e.course.id from Enrollment e where e.student.id = :studentId)
              and a.dueDate between :from and :to
            order by a.dueDate asc
            """)
    List<Assignment> findUpcomingForStudent(@Param("studentId") Long studentId,
                                            @Param("from") LocalDate from,
                                            @Param("to") LocalDate to);
}
