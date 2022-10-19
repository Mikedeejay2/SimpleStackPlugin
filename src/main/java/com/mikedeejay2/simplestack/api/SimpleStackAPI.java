package com.mikedeejay2.simplestack.api;

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

    private static void validateInitialized() {
        if(!initialized) {
            throw new IllegalArgumentException("Simple Stack API has not been initialized");
        }
    }
}
