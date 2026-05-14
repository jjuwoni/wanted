package com.wanted.springevent.certificate.service;

import com.wanted.springevent.certificate.entity.Certificate;
import com.wanted.springevent.certificate.repository.CertificateRepository;
import com.wanted.springevent.enrollment.entity.Enrollment;
import com.wanted.springevent.enrollment.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CertificateService {

    private final EnrollmentRepository enrollmentRepository;
    private final CertificateRepository certificateRepository;

    /* comment.
    *   Transactional 은 주요 속성들이 있다.
    *   - propagation : 전파의 의미를 가지고 있고 default value 는
    *   REQUIRED (상위 트랜잭션이 있다면 같이 묶인다.)
    *   Requires_NEW (상위 트랜잭션과 분리 된 독립 된 트랜잭션 경계를 만든다.)
    *   - rollbackFor : 예외는 크게 2가지가 있다. 언체크 예외, 체크 예외
    *   체크 예외 시에도 rollback 하기 위해서 작성해야 한다.
    *   ex) rollbackFor = Exception.class
    *   - timeout = 3
    *   너무 오래 걸리는 작업을 제한할 때 사용한다.
    * */

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void issueAfterCourseCompletion(Long enrollmentId, String courseTitle, String codePrefix) {
        if (certificateRepository.existsByEnrollment_EnrollmentId(enrollmentId)) {
            log.info("[certificateService] 이미 수료증이 존재하므로 발급을 건너뜁니다. enrollmentId={}", enrollmentId);
            return;
        }

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException("수강 정보를 찾을 수 없습니다. enrollmentId=" + enrollmentId));

        Certificate certificate = certificateRepository.save(
                Certificate.issue(enrollment, codePrefix + "-" + UUID.randomUUID())
        );

        log.info(
                "[certificateService] 새 트랜잭션에서 수료증 발급 완료. courseTitle={}, certificateId={}",
                courseTitle,
                certificate.getCertificateId()
        );
    }

}
