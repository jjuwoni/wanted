package com.wanted.springasync.section03.async_event;

import com.wanted.springasync.common.support.LectureResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/section03")
public class AsyncEventController {

    private final AsyncEventService asyncEventService;

    /* comment.
     *   수강 완료 이벤트를 발행하며, 비동기 리스너가 후처리를 수행한다.
     * */
    @PostMapping("/enrollments/{enrollmentId}/completion")
    public LectureResponse complete(@PathVariable Long enrollmentId) {
        return asyncEventService.completeEnrollment(enrollmentId);
    }

    /* comment.
    *   CompletableFuture 반환형은 비동기 작업의 결과/예외를 나중에
    *   이어서 처리할 수 있게 된다.
    * */
    @PostMapping("/enrollments/{enrollmentId}/completion-summary")
    public CompletionSummaryResponse completeSummary(@PathVariable Long enrollmentId) {
        return asyncEventService.requestCompletionSummary(enrollmentId);
    }

    /* comment.
    *   메인흐름에서 비동기 작업의 결과가 응답 시에 꼭 필요하다면, join() 으로 기다린 뒤
    *   응답할 수 있다.
    *   단, join() 을 쓰게 되면 비동기 작업 수행 시간만큼 응답 시간이 길어진다.
    * */
    @PostMapping("/enrollments/{enrollmentId}/completion-summary/wait")
    public CompletionSummaryResponse completeSummaryWait(@PathVariable Long enrollmentId) {
        return asyncEventService.waitCompletionSummary(enrollmentId);
    }

}
