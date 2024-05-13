package com.mikedeejay2.simplestack.bytecode;

import com.google.common.collect.ImmutableList;
import com.mikedeejay2.simplestack.SimpleStack;
import com.mikedeejay2.simplestack.util.BlacklistPrintStream;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.ResettableClassFileTransformer;

import java.io.PrintStream;
import java.lang.instrument.Instrumentation;
import java.util.List;

/**
 * Simple holder class for initializing the Byte Buddy agent and storing its instrumentation instance.
 *
 * @author Mikedeejay2
 * @since 2.0.0
 */
public final class ByteBuddyHolder {
    private static boolean markedProblem = false;

    /**
     * The stored {@link Instrumentation} of the Byte Buddy agent
     */
    private static Instrumentation instrumentation;

    /**
     * Used with {@link BlacklistPrintStream} to prevent printing the warning seen below on Java 21 and above.
     */
    private static final List<String> printBlacklist = ImmutableList.of(
        "WARNING: A Java agent has been loaded dynamically",
        "WARNING: If a serviceability tool is in use, please run with -XX:+EnableDynamicAgentLoading to hide this warning",
        "WARNING: If a serviceability tool is not in use, please run with -Djdk.instrument.traceUsage for more information",
        "WARNING: Dynamic loading of agents will be disallowed by default in a future release"
    );

    /**
     * Install the {@link ByteBuddyAgent}
     *
     * @return Whether a failure has occurred
     */
    public static boolean install() {
        try {
            instrumentation = ByteBuddyAgent.getInstrumentation();
        } catch(IllegalStateException ignored) {
            // ignored
        }

        if(instrumentation != null) {
            return false;
        }
        // Redirect System.err to stop output warning
        final PrintStream errStream = System.err;
        System.setErr(new BlacklistPrintStream(errStream, printBlacklist));

        boolean result = true;
        try {
            instrumentation = ByteBuddyAgent.install();
            result = false;
        } catch(IllegalStateException ignored) {
            // ignored
        }
        // Change System.err back to original
        System.setErr(errStream);

        return result;
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
