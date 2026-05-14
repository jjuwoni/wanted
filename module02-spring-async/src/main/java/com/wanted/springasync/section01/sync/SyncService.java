package com.wanted.springasync.section01.sync;

import com.wanted.springasync.common.support.LectureResponse;
import com.wanted.springasync.domain.course.Enrollment;
import com.wanted.springasync.repository.course.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SyncService {

    private final EnrollmentRepository enrollmentRepository;
    private final SyncNotificationService syncNotificationService;

    @Transactional
    public LectureResponse completeEnrollment(Long enrollmentId) {

        long start = System.currentTimeMillis();

        Enrollment enrollment = enrollmentRepository.findDetailById(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException("수강 정보를 찾을 수 없습니다. id=" + enrollmentId));

        // 핵심 개념: 동기 처리에서는 메일 발송 같은 후처리도 같은 요청 흐름에 포함된다.
        // 따라서 후처리가 오래 걸리면 API 응답 시간도 그대로 길어진다.
        enrollment.complete();
        syncNotificationService.sendCompletionEmail(enrollment);

        return LectureResponse.completed(
                "section01_sync",
                "동기 방식은 모든 후처리가 끝난 뒤에 응답합니다.",
                start
        );

    }
}
