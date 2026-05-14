package com.wanted.springevent.section02.listener;

import com.wanted.springevent.certificate.entity.Certificate;
import com.wanted.springevent.certificate.repository.CertificateRepository;
import com.wanted.springevent.enrollment.entity.Enrollment;
import com.wanted.springevent.enrollment.repository.EnrollmentRepository;
import com.wanted.springevent.section02.event.Section02CourseCompletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class Section02CertificateEventListener {

    private static final Logger log = LoggerFactory.getLogger(Section02CertificateEventListener.class);

    /* comment.
    *   Listener 는 Event 를 구독하다가 Event 가 발행되면 자동으로 실행된다.
    *   Event 발행자는 Event 처리자를 몰라도 협력을 할 수 있게 된다.
    * */

    /* comment.
    *   주의! 현재는 학습 관계로 repository 를 가지고 있지만
    *   추후 개발 시에는 service 를 가지고 있는 구조로 변경해야 한다.
    * */
    private final EnrollmentRepository enrollmentRepository;
    private final CertificateRepository certificateRepository;

    public Section02CertificateEventListener(
            EnrollmentRepository enrollmentRepository,
            CertificateRepository certificateRepository
    ) {
        this.enrollmentRepository = enrollmentRepository;
        this.certificateRepository = certificateRepository;
    }

    /* comment.
    *   @EventListener
    *   - 해당 어노테이션은 ApplicationEventPublisher 에 등록 된 이벤트를
    *   - 구독하는 역할을 하게 된다.
    * */
    @EventListener
    @Order(-5)
    public void eventHandle(Section02CourseCompletedEvent event) {
        if (certificateRepository.existsByEnrollment_EnrollmentId(event.enrollmentId())) {
            log.info("[section02] 이미 수료증이 존재하므로 발급을 건너뜁니다. enrollmentId={}", event.enrollmentId());
            return;
        }

        Enrollment enrollment = enrollmentRepository.findById(event.enrollmentId())
                .orElseThrow(() -> new IllegalArgumentException("수강 정보를 찾을 수 없습니다. enrollmentId=" + event.enrollmentId()));

        Certificate certificate = certificateRepository.save(
                Certificate.issue(enrollment, "EVENT-" + UUID.randomUUID())
        );

        log.info(
                "[section02] 이벤트 수신 후 수료증 발급 완료. enrollmentId={}, certificateId={}",
                event.enrollmentId(),
                certificate.getCertificateId()
        );
    }
}
