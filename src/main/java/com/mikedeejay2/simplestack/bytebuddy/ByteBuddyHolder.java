package com.mikedeejay2.simplestack.bytebuddy;

import net.bytebuddy.agent.ByteBuddyAgent;
import java.lang.instrument.Instrumentation;

/**
 * Simple holder class for initializing the Byte Buddy agent and storing its instrumentation instance.
 *
 * @author Mikedeejay2
 * @since 2.0.0
 */
public class ByteBuddyHolder {
    /**
     * The stored {@link Instrumentation} of the Byte Buddy agent
     */
    private static Instrumentation instrumentation;

    /**
     * Install the {@link ByteBuddyAgent}
     *
     * @return Whether a failure has occurred
     */
    public static boolean initialize() {
        try {
            instrumentation = ByteBuddyAgent.getInstrumentation();
        } catch(IllegalStateException ignored) {
            // ignored
        }
        if(instrumentation == null) {
            try {
                instrumentation = ByteBuddyAgent.install();
            } catch(IllegalStateException ignored) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the Byte Buddy agent {@link Instrumentation} instance.
     *
     * @return The Byte Buddy agent {@link Instrumentation}
     */
    public static Instrumentation getInstrumentation() {
        return instrumentation;
    }
}
