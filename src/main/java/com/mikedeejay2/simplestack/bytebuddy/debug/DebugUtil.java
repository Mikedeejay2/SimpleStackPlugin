package com.mikedeejay2.simplestack.bytebuddy.debug;

import com.mikedeejay2.simplestack.SimpleStack;
import com.mikedeejay2.simplestack.config.DebugConfig;

public final class DebugUtil {
    private DebugUtil() {
        throw new UnsupportedOperationException("DebugUtil cannot be instantiated");
    }


    /**
     * Given that {@link DebugConfig#isPrintTimings()} is true, print the milliseconds that an operation took to
     * complete.
     *
     * @param startTime The start time of the method
     */
    public static void printTimings(long startTime, String name) {
        if(!SimpleStack.getInstance().getDebugConfig().isPrintTimings()) return;
        long endTime = System.nanoTime();
        SimpleStack.getInstance().sendInfo(String.format(
            "%s took %.4f milliseconds to complete",
            name, ((endTime - startTime) / 1000000.0)));
    }
}
