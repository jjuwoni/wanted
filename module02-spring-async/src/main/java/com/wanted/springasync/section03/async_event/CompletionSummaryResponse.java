package com.wanted.springasync.section03.async_event;

public record CompletionSummaryResponse(
        String responseType,
        String mainFlowResult,
        String asyncFlowResult,
        String threadName,
        long elapsedMillis
) {
    public static CompletionSummaryResponse accepted(String mainFlowResult, long startTimeMillis) {
        return new CompletionSummaryResponse(
                "MAIN_FLOW_ONLY",
                mainFlowResult,
                "비동기 결과는 아직 응답에 포함하지 않고, 완료 후 콜백 로그에서 확인합니다.",
                Thread.currentThread().getName(),
                System.currentTimeMillis() - startTimeMillis
        );
    }

    public static CompletionSummaryResponse completed(String mainFlowResult, String asyncFlowResult, long startTimeMillis) {
        return new CompletionSummaryResponse(
                "MAIN_FLOW_WITH_ASYNC_RESULT",
                mainFlowResult,
                asyncFlowResult,
                Thread.currentThread().getName(),
                System.currentTimeMillis() - startTimeMillis
        );
    }
}
