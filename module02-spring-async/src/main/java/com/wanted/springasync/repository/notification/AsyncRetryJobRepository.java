package com.wanted.springasync.repository.notification;

import com.wanted.springasync.section04.async_exception.AsyncRetryJob;
import com.wanted.springasync.section04.async_exception.AsyncRetryJobStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AsyncRetryJobRepository extends JpaRepository<AsyncRetryJob, Long> {
    List<AsyncRetryJob> findTop20ByStatusAndNextRetryAtLessThanEqualOrderByIdAsc(AsyncRetryJobStatus asyncRetryJobStatus, LocalDateTime now);
}
