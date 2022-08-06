package com.mikedeejay2.simplestack.bytebuddy;

import com.mikedeejay2.simplestack.SimpleStack;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.ResettableClassFileTransformer;

import java.lang.instrument.Instrumentation;

/**
 * Simple holder class for initializing the Byte Buddy agent and storing its instrumentation instance.
 *
 * @author Mikedeejay2
 * @since 2.0.0
 */
public class ByteBuddyHolder {
    private static boolean markedProblem = false;

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

    public static void resetTransformer(ResettableClassFileTransformer transformer) {
        if(transformer != null) {
            try {
                transformer.reset(ByteBuddyHolder.getInstrumentation(), AgentBuilder.RedefinitionStrategy.RETRANSFORMATION);
            } catch(NoClassDefFoundError e) {
                if(!markedProblem) {
                    SimpleStack.getInstance().sendWarning("&eAn issue occurred while resetting a transformer, server reloaded?");
                    SimpleStack.getInstance().sendWarning("&eIf Simple Stack begins acting abnormally, restart the server");
                }
                markedProblem = true;
            }
            ByteBuddyHolder.getInstrumentation().removeTransformer(transformer);
        }
    }
}
