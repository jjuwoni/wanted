package com.wanted.springasync.section01.sync;

import com.wanted.springasync.common.support.LectureResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/section01")
public class SyncController {

    private final SyncService service;

    /* comment.
    *   해당 섹션은 수강이 완료되고 동기적으로 메일 발송을 하는 시나리오
    * */
    @PostMapping("/enrollments/{enrollmentId}/completion")
    public LectureResponse complete(@PathVariable Long enrollmentId) {
        return service.completeEnrollment(enrollmentId);
    }
}
