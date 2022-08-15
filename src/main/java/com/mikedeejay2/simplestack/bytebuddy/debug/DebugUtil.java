package com.mikedeejay2.simplestack.bytebuddy.debug;

import com.mikedeejay2.simplestack.SimpleStack;

public final class DebugUtil {
    private DebugUtil() {
        throw new UnsupportedOperationException("DebugUtil cannot be instantiated");
    }

    public static void printTimings(long startTime, String name) {
        if(!SimpleStack.getInstance().getDebugConfig().isPrintTimings()) return;
        long endTime = System.nanoTime();
        SimpleStack.getInstance().sendInfo(String.format(
            "%s took %.4f milliseconds to complete",
            name, ((endTime - startTime) / 1000000.0)));
    }
}
