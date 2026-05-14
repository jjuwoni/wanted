package com.wanted.springevent.common.run;

import com.wanted.springevent.certificate.repository.CertificateRepository;
import com.wanted.springevent.section01.Section01CourseCompletionService;
import com.wanted.springevent.section02.service.Section02CourseCompletionService;
import com.wanted.springevent.section03.service.Section03CourseCompletionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/* comment.
*   해당 클래스는 Demo 시나리오를 테스트하는 클래스다.
*   어플리케이션을 실행하면 해당 클래스가
*   시나리오를 수행한다.
* */

@Component
public class DemoRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoRunner.class);

    private final DemoScenarioFactory demoScenarioFactory;
//    private final Section01CourseCompletionService section01CourseCompletionService;
//    private final Section02CourseCompletionService section02CourseCompletionService;
    private final Section03CourseCompletionService section03CourseCompletionService;
    private final CertificateRepository certificateRepository;

    public DemoRunner(
            DemoScenarioFactory demoScenarioFactory,
//            Section01CourseCompletionService section01CourseCompletionService,
//            Section02CourseCompletionService section02CourseCompletionService,
            Section03CourseCompletionService section03CourseCompletionService,
            CertificateRepository certificateRepository
    ) {
        this.demoScenarioFactory = demoScenarioFactory;
//        this.section01CourseCompletionService = section01CourseCompletionService;
//        this.section02CourseCompletionService = section02CourseCompletionService;
        this.section03CourseCompletionService = section03CourseCompletionService;
        this.certificateRepository = certificateRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        DemoScenario scenario = demoScenarioFactory.createScenario();

//        log.info("========== section01: 이벤트 없이 직접 처리 ==========");
//        section01CourseCompletionService.completeCourse(scenario.section01EnrollmentId());
//        logCertificateCount();

//        log.info("========== section02: @EventListener로 발행/구독 분리 ==========");
//        section02CourseCompletionService.completeCourse(scenario.section02EnrollmentId());
//        logCertificateCount();

        log.info("========== section03: AFTER_COMMIT 이후 후속 처리 ==========");
        section03CourseCompletionService.completeCourse(scenario.section03EnrollmentId());
        logCertificateCount();
    }

    private void logCertificateCount() {
        log.info("[runner] 현재 발급된 수료증 수={}", certificateRepository.count());
    }
}
