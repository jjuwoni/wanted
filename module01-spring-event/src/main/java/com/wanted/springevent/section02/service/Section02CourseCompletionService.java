package com.wanted.springevent.section02.service;

import com.wanted.springevent.certificate.repository.CertificateRepository;
import com.wanted.springevent.enrollment.entity.Enrollment;
import com.wanted.springevent.enrollment.repository.EnrollmentRepository;
import com.wanted.springevent.section01.Section01CourseCompletionService;
import com.wanted.springevent.section02.event.Section02CourseCompletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class Section02CourseCompletionService {

    /* comment.
    *   Section02 은 Course 도메인의 핵심 기능인 수강 완료 처리만 담당한다.
    *   수료증 발급 기능과 로그 기능은 핵심이 아닌 후속 기능이라고 판단을 하고
    *   직접 비즈니스 로직으로 관리하는 것이 아닌 "수강 완료가 발생했다"
    *   라는 사실만 이벤트로 발생시킬 것이다.
    *   ----
    *   [중요한 점]
    *   객체와 객체 간의 의존성을 제거하는 행위에 있어서
    *   Event 가 만능처럼 보일 수 있다.
    *   의존성을 제거해서 모든 기능을 Event 방식으로 바꾸게 되면
    *   @Transactional 경계가 무너지게 된다.
    *   따라서 Event 를 만들 때 신경써야 하는 부분은
    *   @Transactional 의 전파 혹은 예외 발생 시 처리가
    *   굉장히 중요해진다.
    * */

    private static final Logger log = LoggerFactory.getLogger(Section02CourseCompletionService.class);

    private final EnrollmentRepository enrollmentRepository;

    // Event 발행자
    private final ApplicationEventPublisher publisher;

    // 생성자로 의존성 주입
    public Section02CourseCompletionService(EnrollmentRepository enrollmentRepository, ApplicationEventPublisher publisher) {
        this.enrollmentRepository = enrollmentRepository;
        this.publisher = publisher;

    }

    @Transactional
    public void completeCourse(Long enrollmentId) {
        // User, Course 정보를 enrollmentId 로 판단하여 수강이 완료됨을 처리할 것이다.
        Enrollment enrollment = enrollmentRepository.findWithUserAndCourseByEnrollmentId(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException("수강 정보를 찾을 수 없습니다. enrollmentId = " + enrollmentId));

        // 수강 상태를 완료로 변환
        enrollment.complete();

        log.info("[section02] Course 서비스는 수강 완료 처리 후 이벤트만 발행한다.");

        // publisher 를 이용해서 "수강 완료" 이벤트 발행
        publisher.publishEvent(
                new Section02CourseCompletedEvent(
                        enrollment.getEnrollmentId(),
                        enrollment.getUser().getUserId(),
                        enrollment.getUser().getName(),
                        enrollment.getCourse().getCourseId(),
                        enrollment.getCourse().getTitle()
                )
        );


    }

}
