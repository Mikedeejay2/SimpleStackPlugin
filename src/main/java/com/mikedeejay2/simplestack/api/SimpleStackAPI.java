package com.mikedeejay2.simplestack.api;

public final class SimpleStackAPI {
    private static SimpleStackConfig config;
    private static SimpleStackTimings timings;

    public static SimpleStackConfig getConfig() {
        return config;
    }

    public static SimpleStackTimings getTimings() {
        return timings;
    }
}
