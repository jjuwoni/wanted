package com.wanted.springasync.repository.course;

import com.wanted.springasync.domain.course.Enrollment;
import com.wanted.springasync.domain.course.EnrollmentStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    long countByStatus(EnrollmentStatus status);

    @Query("""
            select e
            from Enrollment e
            join fetch e.user
            join fetch e.course
            where e.id = :enrollmentId
            """)
    Optional<Enrollment> findDetailById(@Param("enrollmentId") Long enrollmentId);
}
