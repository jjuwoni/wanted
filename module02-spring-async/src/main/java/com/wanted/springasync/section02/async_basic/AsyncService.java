package com.wanted.springasync.section02.async_basic;

import com.wanted.springasync.common.support.LectureResponse;
import com.wanted.springasync.domain.course.Enrollment;
import com.wanted.springasync.repository.course.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncService {

    private final EnrollmentRepository enrollmentRepository;
    private final AsyncNotificationService asyncNotificationService;

    @Transactional
    public LectureResponse completeEnrollment(Long enrollmentId) {

        long start = System.currentTimeMillis();

        Enrollment enrollment = enrollmentRepository.findDetailById(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException("수강 정보를 찾을 수 없습니다. id=" + enrollmentId));

        enrollment.complete();

        /* comment.
        *   해당 작업은 메인 흐름과는 별개로 부가적인 기능이기 때문에
        *   별도의 흐름에서 진행되어 메인 흐름에 지장이 덜 가게 만들고자 한다.
        * */
        asyncNotificationService.sendCompletionEmail(enrollment);
        log.info("[section02] 비동기 메소드 호출 후 실행되는 LOG");

        return LectureResponse.completed(
                "section02_async",
                "@Async 호출 후에는 메인 요청이 먼저 응답하고 메일 작업은 별도의 쓰레드에서 진행된다.",
                start
        );

    }
}
