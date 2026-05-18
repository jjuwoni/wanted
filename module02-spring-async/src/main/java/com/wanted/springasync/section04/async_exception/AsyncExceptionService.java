package com.wanted.springasync.section04.async_exception;

import com.wanted.springasync.common.support.SleepUtils;
import com.wanted.springasync.domain.notification.NotificationLog;
import com.wanted.springasync.domain.user.User;
import com.wanted.springasync.repository.notification.AsyncRetryJobRepository;
import com.wanted.springasync.repository.notification.NotificationLogRepository;
import com.wanted.springasync.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AsyncExceptionService {

    private final AsyncRetryJobRepository asyncRetryJobRepository;
    private final UserRepository userRepository;
    private final NotificationLogRepository notificationLogRepository;

    @Async
    public void voidExceptionAsync() {

        log.info("[section04] void 반환형 비동기 메소드 호출됨..thread={}", Thread.currentThread().getName());

        SleepUtils.sleep(1000L);

        throw new IllegalArgumentException("❌ void 반환 타입의 비동기 메서드 예외 발생!!! ❌");


//        log.info("[section04] void 반환형 비동기 메소드 종료됨..thread={}", Thread.currentThread().getName());
    }

    public AsyncRetryJobResponse createRetryDemoJob(Long userId, int failUntilAttempt, int maxRetryCount) {

        // 해당 메서드는 임시 데이터 Insert 용 메서드이다.
        AsyncRetryJob job = AsyncRetryJob.createEmailJob(
                userId, "section04 재시도 관련 예제 메일 데이터", maxRetryCount, failUntilAttempt
        );

        AsyncRetryJob savedJob = asyncRetryJobRepository.save(job);

        return AsyncRetryJobResponse.from(savedJob);
    }

    // 비동기 방식으로 실패한 작업 재시도
    @Async("classTaskExecutor")
    @Transactional
    public void processRetryJobAsync(Long jobId) {
        AsyncRetryJob job = asyncRetryJobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("재시도 작업을 찾을 수 없습니다. id=" + jobId));

        try {
            log.info(
                    "[section04] 재시도 작업 처리 시작. jobId={}, retryCount={}, thread={}",
                    job.getId(),
                    job.getRetryCount(),
                    Thread.currentThread().getName()
            );
            send(job);
            job.markSuccess();
            log.info("[section04] 재시도 작업 성공. jobId={}, retryCount={}, status={}", job.getId(), job.getRetryCount(), job.getStatus());
        } catch (Exception exception) {
            job.markRetry(exception.getMessage(), 5L);
            log.warn(
                    "[section04] 재시도 작업 실패. jobId={}, retryCount={}, status={}, message={}",
                    job.getId(),
                    job.getRetryCount(),
                    job.getStatus(),
                    exception.getMessage()
            );
        }
    }

    private void send(AsyncRetryJob job) {
        if (job.shouldFailThisAttempt()) {
            throw new IllegalStateException("일시 실패를 가정한 예외입니다.");
        }

        User user = userRepository.findById(job.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다. id=" + job.getUserId()));
        notificationLogRepository.save(NotificationLog.email(user, job.getMessage()));
    }

    public List<AsyncRetryJob> findRetryTargets() {
        return asyncRetryJobRepository.findTop20ByStatusAndNextRetryAtLessThanEqualOrderByIdAsc(
                AsyncRetryJobStatus.PENDING,
                LocalDateTime.now()
        );
    }
}
