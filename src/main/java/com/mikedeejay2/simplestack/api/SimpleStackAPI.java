package com.mikedeejay2.simplestack.api;

import org.apache.commons.lang3.Validate;

public final class SimpleStackAPI {
    private static SimpleStackConfig config;

    public static SimpleStackConfig getConfig() {
        return config;
    }

    public static void setConfig(SimpleStackConfig config) {
        Validate.isTrue(SimpleStackAPI.config == null, "Attempted to change singleton Simple Stack config");
        SimpleStackAPI.config = config;
    }
}
