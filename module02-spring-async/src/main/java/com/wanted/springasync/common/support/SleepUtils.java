package com.wanted.springasync.common.support;

public final class SleepUtils {

    private SleepUtils() {
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("sleep interrupted", exception);
        }
    }
}
