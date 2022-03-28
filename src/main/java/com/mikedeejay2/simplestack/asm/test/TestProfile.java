package com.mikedeejay2.simplestack.asm.test;

/**
 * @see <a href=https://bukkit.org/threads/tutorial-extreme-beyond-reflection-asm-replacing-loaded-classes.99376/>https://bukkit.org/threads/tutorial-extreme-beyond-reflection-asm-replacing-loaded-classes.99376/</a>
 */
public class TestProfile {
    public static void start(String className, String methodName) {
        System.out.printf("%s\t%s\tstart\t%d%n", className, methodName, System.currentTimeMillis());
    }

    public static void end(String className, String methodName) {
        System.out.printf("%s\t%s\tend\t%d%n", className, methodName, System.currentTimeMillis());
    }
}
