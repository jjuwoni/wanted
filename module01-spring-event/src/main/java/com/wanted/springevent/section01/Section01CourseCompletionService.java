package com.wanted.springevent.section01;

import com.wanted.springevent.certificate.entity.Certificate;
import com.wanted.springevent.certificate.repository.CertificateRepository;
import com.wanted.springevent.enrollment.entity.Enrollment;
import com.wanted.springevent.enrollment.repository.EnrollmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class Section01CourseCompletionService {

    private static final Logger log = LoggerFactory.getLogger(Section01CourseCompletionService.class);

    /* comment. 시나리오
    *   CourseService 클래스는 수강 완료 처리와 수료증 발급을 모두 담당한다.
    *   CourseService 는 고로 Enrollment 와 Certificate 도메인을 모두 의존해야 한다.
    *   ---
    *   수업 시에는 EnrollmentService, CertificateService 는 생략하고
    *   바로 Repository 계층을 호출하는 것으로 축약한다!
    * */

    private final EnrollmentRepository enrollmentRepository;
    private final CertificateRepository certificateRepository;

    // 생성자로 의존성 주입
    public Section01CourseCompletionService(EnrollmentRepository enrollmentRepository,
                                            CertificateRepository certificateRepository) {
        this.certificateRepository = certificateRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    // 강의 완료 메서드
    /* comment.
    *   CourseService 에서 수강 관련, 수료증 관련 로직을 컨트롤 하고 있다.
    *   강의를 본다는 행위가 Course 도메인에서는 "주" 인 기능이고
    *   수강 완료, 수료증 발급 행위는 "부" 인 기능이다.
    * */
    @Transactional
    public void completeCourse(Long enrollmentId) {

        // User, Course 정보를 enrollmentId 로 판단하여 수강이 완료됨을 처리할 것이다.
        Enrollment enrollment = enrollmentRepository.findWithUserAndCourseByEnrollmentId(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException("수강 정보를 찾을 수 없습니다. enrollmentId = " + enrollmentId));

        // 수강 상태를 완료로 변환
        enrollment.complete();

        log.info("[section01] Course 서비스가 수강완료 처리와 수료증 발급 처리를 수행함.");

        // 수료증 객체 생성
        Certificate certificate = Certificate.issue(enrollment, generateCode("SUCCESS"));

        // 만들어진 수료증 객체를 영속성 컨텍스트에 저장
        certificateRepository.save(certificate);

        log.info("[section01] 수료증 발급 완료. certificateId={}", certificate.getCertificateId());
    }

    // 단순 수료증 발급 시 발급 코드를 만들어주는  내부 헬퍼 메서드
    private String generateCode(String prefix) {
        return prefix + "-" + UUID.randomUUID();
    }

}
