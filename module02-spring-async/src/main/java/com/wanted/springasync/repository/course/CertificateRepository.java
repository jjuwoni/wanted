package com.wanted.springasync.repository.course;

import com.wanted.springasync.domain.course.Certificate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {

    boolean existsByEnrollmentId(Long enrollmentId);

    Optional<Certificate> findByEnrollmentId(Long enrollmentId);
}
