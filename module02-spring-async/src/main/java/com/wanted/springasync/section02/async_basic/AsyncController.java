package com.wanted.springasync.section02.async_basic;

import com.wanted.springasync.common.support.LectureResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/section02")
@RequiredArgsConstructor
public class AsyncController {

    private final AsyncService asyncService;

    @PostMapping("/enrollments/{enrollmentId}/completion")
    public LectureResponse complete(@PathVariable Long enrollmentId) {
        return asyncService.completeEnrollment(enrollmentId);
    }

}
