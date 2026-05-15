package com.wanted.springasync.section03.async_event;

import com.wanted.springasync.common.support.SleepUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class CompletionSummaryService {

    /* comment.
    *   비동기 메서드의 반환형 차이
    *   1. void : 비동기 메서드 호출하는 곳은 비동기 완료 결과를 받을 수 없다.
    *   2. CompletableFuture : 비동기 메서드 호출하는 곳은 thenAccept(), join(), get() 등으로
    *   비동기 메서드 완료 결과를 이어서 다룰 수 있게 된다.
    * */

    @Async
    public CompletableFuture<String> createSummaryAsync(Long enrollmentId) {

        log.info("[section03] 🚨비동기🚨 CompletableFuture 수강 완료 시 진행되는 이벤트 작업 시작!! 작업 중인 Thread = {}", Thread.currentThread().getName());

        SleepUtils.sleep(3000L);
        // 실제 프로젝트 시에ㅐ는 DB 에 요약본이 저장되거나, 조회 하는 형태로 변경한다.
        String summary = "enrollmentId = " + enrollmentId + "수강 완료 요약본 생성됨!!!";

        log.info("[section03] 🚨비동기🚨 CompletableFuture 수강 완료 시 진행되는 이벤트 작업 종료!! 작업 중인 Thread = {}", Thread.currentThread().getName());

        return CompletableFuture.completedFuture(summary);
    }
}
