package com.mikedeejay2.simplestack.bytecode;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.lang.invoke.*;
import java.lang.reflect.Method;

/**
 * A bridge class to access SimpleStack advice methods via Minecraft's ClassLoader.
 * <p>
 * Uses {@link MethodHandle} to invoke methods as it's faster than reflection. Use of {@link LambdaMetafactory} would
 * most likely be faster in this context because {@link MethodHandle} is not inlined (final) in this instance for plugin
 * reloading purposes. LambdaMetafactory, however, does not work because of lack of support for differing ClassLoaders.
 * <p>
 * Benchmark of Reflection vs MethodHandle performance:
 * <table>
 *     <tr>
 *         <th>Ms/tick (1 minute)</th>
 *         <th>Minimum</th>
 *         <th>Medium</th>
 *         <th>Average</th>
 *         <th>95th percentile</th>
 *         <th>Maximum</th>
 *     </tr>
 *     <tr>
 *         <td>Reflection</td>
 *         <td>2.03ms</td>
 *         <td>2.39ms</td>
 *         <td>4.54ms</td>
 *         <td>21.82ms</td>
 *         <td>38.53ms</td>
 *     </tr>
 *     <tr>
 *         <td>Reflection</td>
 *         <td>3.62ms</td>
 *         <td>4.32ms</td>
 *         <td>7.28ms</td>
 *         <td>24.27ms</td>
 *         <td>33.45ms</td>
 *     </tr>
 *     <tr>
 *         <td>Handle</td>
 *         <td>0.87ms</td>
 *         <td>0.99ms</td>
 *         <td>1.83ms</td>
 *         <td>7.62ms</td>
 *         <td>12.82ms</td>
 *     </tr>
 *     <tr>
 *         <td>Handle</td>
 *         <td>1.85ms</td>
 *         <td>2.16ms</td>
 *         <td>3.41ms</td>
 *         <td>10.57ms</td>
 *         <td>15.60ms</td>
 *     </tr>
 * </table>
 *
 * @author Mikedeejay2
 */
public final class AdviceBridge {
    private static MethodHandle getArmorSlotMaxStackSize;
    private static MethodHandle getItemMaxStackSize;
    private static MethodHandle getItemStackMaxStackSize;
    private static MethodHandle getSlotMaxStackSize;
    private static MethodHandle getSlotISMaxStackSize;
    private static MethodHandle getBukkitMaterialMaxStackSize;
    private static MethodHandle getBukkitItemStackMaxStackSize;
    private static MethodHandle getCraftBukkitItemStackMaxStackSize;

    public static void initialize() {
        final Plugin plugin = Bukkit.getPluginManager().getPlugin("SimpleStack");
        final ClassLoader classLoader = plugin.getClass().getClassLoader();
        try {
            getArmorSlotMaxStackSize = getMethodHandle("TransformArmorSlotGetMaxStackSize", classLoader, "getArmorSlotMaxStackSize", Object.class);
            getItemMaxStackSize = getMethodHandle("TransformItemGetMaxStackSize", classLoader, "getItemMaxStackSize", Object.class);
            getItemStackMaxStackSize = getMethodHandle("TransformItemStackGetMaxStackSize", classLoader, "getItemStackMaxStackSize", Object.class);
            getSlotMaxStackSize = getMethodHandle("TransformSlotGetMaxStackSize", classLoader, "getSlotMaxStackSize", Object.class);
            getSlotISMaxStackSize = getMethodHandle("TransformSlotISGetMaxStackSize", classLoader, "getSlotMaxStackSize", Object.class, Object.class);
            getBukkitMaterialMaxStackSize = getMethodHandle("TransformBukkitMaterialGetMaxStackSize", classLoader, "getBukkitMaterialMaxStackSize", Material.class);
            getBukkitItemStackMaxStackSize = getMethodHandle("TransformBukkitItemStackGetMaxStackSize", classLoader, "getBukkitItemStackMaxStackSize", ItemStack.class);
            getCraftBukkitItemStackMaxStackSize = getMethodHandle("TransformCraftBukkitItemStackGetMaxStackSize", classLoader, "getCraftBukkitItemStackMaxStackSize", ItemStack.class);
        } catch(Throwable e) {
            throw new RuntimeException(e);
        }
    }
    private static MethodHandle getMethodHandle(String className, ClassLoader classLoader, String methodName, Class<?>... args) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException {
        final Method method = getMethod(className, classLoader, methodName, args);
        final MethodHandles.Lookup lookup = MethodHandles.lookup();
        return lookup.unreflect(method);
    }

    private static Method getMethod(String className, ClassLoader classLoader, String methodName, Class<?>... args) throws ClassNotFoundException, NoSuchMethodException {
        final Class<?> clazz = Class.forName("com.mikedeejay2.simplestack.bytecode.transformers.advice." + className, false, classLoader);
        final Class<?>[] newArgs = new Class<?>[args.length + 2];
        System.arraycopy(new Class<?>[]{int.class, long.class}, 0, newArgs, 0, 2);
        System.arraycopy(args, 0, newArgs, 2, args.length);
        final Method method = clazz.getMethod(methodName, newArgs);
        method.setAccessible(true);
        return method;
    }

    public static int getArmorSlotMaxStackSize(int currentReturnValue, long startTime, Object nmsSlot) throws Throwable {
        return (int) getArmorSlotMaxStackSize.invokeExact(currentReturnValue, startTime, nmsSlot);
    }

    public static int getItemMaxStackSize(int currentReturnValue, long startTime, Object nmsItem) throws Throwable {
        return (int) getItemMaxStackSize.invokeExact(currentReturnValue, startTime, nmsItem);
    }

    public static int getItemStackMaxStackSize(int currentReturnValue, long startTime, Object nmsItemStack) throws Throwable {
        return (int) getItemStackMaxStackSize.invokeExact(currentReturnValue, startTime, nmsItemStack);
    }

    public static int getSlotMaxStackSize(int currentReturnValue, long startTime, Object nmsSlot) throws Throwable {
        return (int) getSlotMaxStackSize.invokeExact(currentReturnValue, startTime, nmsSlot);
    }

    public static int getSlotISMaxStackSize(int currentReturnValue, long startTime, Object nmsSlot, Object nmsItemStack) throws Throwable {
        return (int) getSlotISMaxStackSize.invokeExact(currentReturnValue, startTime, nmsSlot, nmsItemStack);
    }

    public static int getBukkitMaterialMaxStackSize(int currentReturnValue, long startTime, Material material) throws Throwable {
        return (int) getBukkitMaterialMaxStackSize.invokeExact(currentReturnValue, startTime, material);
    }

    public static int getBukkitItemStackMaxStackSize(int currentReturnValue, long startTime, ItemStack itemStack) throws Throwable {
        return (int) getBukkitItemStackMaxStackSize.invokeExact(currentReturnValue, startTime, itemStack);
    }

    public static int getCraftBukkitItemStackMaxStackSize(int currentReturnValue, long startTime, ItemStack itemStack) throws Throwable {
        return (int) getCraftBukkitItemStackMaxStackSize.invokeExact(currentReturnValue, startTime, itemStack);
    }
}
