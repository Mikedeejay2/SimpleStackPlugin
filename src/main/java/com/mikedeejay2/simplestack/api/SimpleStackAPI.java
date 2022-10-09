package com.mikedeejay2.simplestack.api;

import org.apache.commons.lang3.Validate;

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
        Validate.isTrue(initialized, "Simple Stack API has not been initialized");
    }
}
