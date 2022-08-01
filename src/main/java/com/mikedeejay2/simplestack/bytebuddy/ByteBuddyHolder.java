package com.mikedeejay2.simplestack.bytebuddy;

import net.bytebuddy.agent.ByteBuddyAgent;
import java.lang.instrument.Instrumentation;

public class ByteBuddyHolder {
    private static Instrumentation instrumentation;

    public static boolean initialize() {
        try {
            instrumentation = ByteBuddyAgent.install();
        } catch(IllegalStateException ignored) {
            return true;
        }
        return false;
    }

    public static Instrumentation getInstrumentation() {
        return instrumentation;
    }
}
