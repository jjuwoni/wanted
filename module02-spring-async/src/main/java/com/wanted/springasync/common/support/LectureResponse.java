package com.wanted.springasync.common.support;

public record LectureResponse(
        String section,
        String message,
        String threadName,
        long elapsedMillis
) {
    public static LectureResponse completed(String section, String message, long startTimeMillis) {
        return new LectureResponse(
                section,
                message,
                Thread.currentThread().getName(),
                System.currentTimeMillis() - startTimeMillis
        );
    }
}
