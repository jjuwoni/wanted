package com.wanted.springasync.section04.async_exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RetryScheduler {

    private final AsyncExceptionService asyncExceptionService;

    @Scheduled(fixedDelayString = "${app.retry.fixed-delay-ms}")
    public void retryJobs() {

        // 재시도를 해야 하는 작업 찾기
        List<AsyncRetryJob> jobs = asyncExceptionService.findRetryTargets();

        // 재시도 작업이 없으면 종료
        if(jobs.isEmpty()) {
            return;
        }

        log.info("[section04] 재시도 대상 {}건을 스케쥴러로 재실행 한다.", jobs.size());

        // 반복문을 통해서 재시도 대상을 재시도 하는 메소드 실행
        jobs.forEach(job -> asyncExceptionService.processRetryJobAsync(job.getId()));

        // 주원이형 사랑해여❤️❤️❤️❤️❤️❤️❤️❤️❤️❤️❤️❤️❤️❤️❤️❤️❤️❤️❤️❤️❤️❤️❤️❤️❤️❤️❤️❤️❤️❤️❤️❤️❤️❤️❤️❤️❤️❤️❤️
        // - 정현 올림
    }

}
