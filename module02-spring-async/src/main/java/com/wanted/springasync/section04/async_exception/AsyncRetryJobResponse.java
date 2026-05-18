package com.wanted.springasync.section04.async_exception;

public record AsyncRetryJobResponse(
        Long jobId,
        String status,
        int retryCount,
        int maxRetryCount,
        String nextRetryAt,
        String lastErrorMessage
) {
    public static AsyncRetryJobResponse from(AsyncRetryJob job) {
        return new AsyncRetryJobResponse(
                job.getId(),
                job.getStatus().name(),
                job.getRetryCount(),
                job.getMaxRetryCount(),
                job.getNextRetryAt().toString(),
                job.getLastErrorMessage()
        );
    }
}
