package com.wanted.springevent.certificate.repository;

import com.wanted.springevent.certificate.entity.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    // 이벤트에서 전달받은 enrollmentId 가 존재하는 지 검증
    boolean existsByEnrollment_EnrollmentId(Long aLong);
}
