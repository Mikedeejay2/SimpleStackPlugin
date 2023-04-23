package com.mikedeejay2.simplestack.api;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * Simple Stack v2 API
 * <p>
 * The source code for Simple Stack can be found here:
 * <a href="https://github.com/Mikedeejay2/SimpleStackPlugin">https://github.com/Mikedeejay2/SimpleStackPlugin</a>
 *
 * @author Mikedeejay2
 * @since 2.0.0
 */
public final class SimpleStackAPI {
    private static SimpleStackConfig config;
    private static SimpleStackTimings timings;
    private static boolean initialized;

    @Contract(value = " -> fail", pure = true)
    private SimpleStackAPI() {
        throw new UnsupportedOperationException("SimpleStackAPI cannot be instantiated as an object.");
    }

    /**
     * Get the {@link SimpleStackConfig} instance for modifying and retrieving data from Simple Stack's configuration.
     *
     * @return The {@link SimpleStackConfig} instance
     * @throws IllegalStateException If the API has not been initialized. Check API availability using
     * {@link SimpleStackAPI#isAvailable()} before calling this method to ensure that this exception isn't thrown.
     * @see SimpleStackConfig
     */
    @Contract(pure = true)
    public static @Nullable SimpleStackConfig getConfig() throws IllegalStateException {
        validateInitialized();
        return config;
    }

    /**
     * Get the {@link SimpleStackTimings} instance for getting timings information for Simple Stack.
     *
     * @return The {@link SimpleStackTimings} instance
     * @throws IllegalStateException If the API has not been initialized. Check API availability using
     * {@link SimpleStackAPI#isAvailable()} before calling this method to ensure that this exception isn't thrown.
     * @see SimpleStackTimings
     */
    @Contract(pure = true)
    public static @Nullable SimpleStackTimings getTimings() throws IllegalStateException {
        validateInitialized();
        return timings;
    }

    /**
     * Get whether the Simple Stack API is available. This method should be checked before calling other methods to
     * ensure that the API is available and ready for use.
     *
     * @return Simple Stack API availability state
     */
    @Contract(pure = true)
    public static boolean isAvailable() {
        return initialized;
    }

    /**
     * Internal method for throwing an {@link IllegalStateException} if the API has not been initialized.
     *
     * @throws IllegalStateException If the API has not been initialized.
     */
    private static void validateInitialized() throws IllegalStateException {
        Preconditions.checkState(initialized, "Simple Stack API has not been initialized");
    }
}
