package com.wanted.springasync.section03.async_event;

// 읽기 전용 수강 완료 이벤트 객체
public record CourseCompletedEvent(
        Long enrollmentId,
        Long userId,
        String courseTitle
) {
}
