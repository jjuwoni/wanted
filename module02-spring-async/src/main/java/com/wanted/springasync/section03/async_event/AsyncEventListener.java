package com.wanted.springasync.section03.async_event;

import com.wanted.springasync.common.support.SleepUtils;
import com.wanted.springasync.domain.notification.NotificationLog;
import com.wanted.springasync.domain.user.User;
import com.wanted.springasync.repository.notification.NotificationLogRepository;
import com.wanted.springasync.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class AsyncEventListener {

    // 현재 수업에서는 학습을 위해 repository 를 직접 호출 할 것이다.
    // 다만 나중 프로젝트 시에는 service 를 호출하는 구조가 올바른 구조이다.
    private final NotificationLogRepository notificationLogRepository;
    private final UserRepository userRepository;

    @Async("classTaskExecutor")
    @EventListener
    public void eventHandler(CourseCompletedEvent event) {

        log.info("[section03] 🚨비동기🚨 수강 완료 시 진행되는 이벤트 작업 시작!! 작업 중인 Thread = {}", Thread.currentThread().getName());

        // 시간 소요 시뮬레이션 3초간 정지한다.
        SleepUtils.sleep(3000L);

        User user = userRepository.findById(event.userId()).orElseThrow(() -> new IllegalArgumentException("사용자 정보 찾을 수 없읍!"));

        notificationLogRepository.save(NotificationLog.email(user, event.courseTitle() + "수강 완료 처리 메일!"));

        log.info("[section03] 🚨비동기🚨 수강 완료 시 진행되는 이벤트 작업 종료!! 작업 중인 Thread = {}", Thread.currentThread().getName());


    }

}
