package com.mikedeejay2.simplestack.api;

import com.google.common.base.Preconditions;

public final class SimpleStackAPI {
    private static SimpleStackConfig config;
    private static SimpleStackTimings timings;
    private static boolean initialized;

    public static SimpleStackConfig getConfig() {
        validateInitialized();
        return config;
    }

    public static SimpleStackTimings getTimings() {
        validateInitialized();
        return timings;
    }

    public static boolean isAvailable() {
        return initialized;
    }

    private static void validateInitialized() {
        Preconditions.checkState(initialized, "Simple Stack API has not been initialized");
    }
}
