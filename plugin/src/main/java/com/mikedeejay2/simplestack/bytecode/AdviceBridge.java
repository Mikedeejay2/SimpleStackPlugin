package com.mikedeejay2.simplestack.bytecode;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class AdviceBridge {
    private static Method getArmorSlotMaxStackSize;
    private static Method getItemMaxStackSize;
    private static Method getItemStackMaxStackSize;
    private static Method getSlotMaxStackSize;
    private static Method getSlotISMaxStackSize;
    private static Method getBukkitMaterialMaxStackSize;
    private static Method getBukkitItemStackMaxStackSize;
    private static Method getCraftBukkitItemStackMaxStackSize;

    public static void initialize() {
        final Plugin plugin = Bukkit.getPluginManager().getPlugin("SimpleStack");
        final ClassLoader classLoader = plugin.getClass().getClassLoader();
        try {
            getArmorSlotMaxStackSize = getMethod("TransformArmorSlotGetMaxStackSize", classLoader, "getArmorSlotMaxStackSize", Object.class);
            getItemMaxStackSize = getMethod("TransformItemGetMaxStackSize", classLoader, "getItemMaxStackSize", Object.class);
            getItemStackMaxStackSize = getMethod("TransformItemStackGetMaxStackSize", classLoader, "getItemStackMaxStackSize", Object.class);
            getSlotMaxStackSize = getMethod("TransformSlotGetMaxStackSize", classLoader, "getSlotMaxStackSize", Object.class);
            getSlotISMaxStackSize = getMethod("TransformSlotISGetMaxStackSize", classLoader, "getSlotMaxStackSize", Object.class, Object.class);
            getBukkitMaterialMaxStackSize = getMethod("TransformBukkitMaterialGetMaxStackSize", classLoader, "getBukkitMaterialMaxStackSize", Material.class);
            getBukkitItemStackMaxStackSize = getMethod("TransformBukkitItemStackGetMaxStackSize", classLoader, "getBukkitItemStackMaxStackSize", ItemStack.class);
            getCraftBukkitItemStackMaxStackSize = getMethod("TransformCraftBukkitItemStackGetMaxStackSize", classLoader, "getCraftBukkitItemStackMaxStackSize", ItemStack.class);
        } catch(ClassNotFoundException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
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

    public static int getArmorSlotMaxStackSize(int currentReturnValue, long startTime, Object nmsSlot) throws InvocationTargetException, IllegalAccessException {
        return (int) getArmorSlotMaxStackSize.invoke(null, currentReturnValue, startTime, nmsSlot);
    }

    public static int getItemMaxStackSize(int currentReturnValue, long startTime, Object nmsItem) throws InvocationTargetException, IllegalAccessException {
        return (int) getItemMaxStackSize.invoke(null, currentReturnValue, startTime, nmsItem);
    }

    public static int getItemStackMaxStackSize(int currentReturnValue, long startTime, Object nmsItemStack) throws InvocationTargetException, IllegalAccessException {
        return (int) getItemStackMaxStackSize.invoke(null, currentReturnValue, startTime, nmsItemStack);
    }

    public static int getSlotMaxStackSize(int currentReturnValue, long startTime, Object nmsSlot) throws InvocationTargetException, IllegalAccessException {
        return (int) getSlotMaxStackSize.invoke(null, currentReturnValue, startTime, nmsSlot);
    }

    public static int getSlotISMaxStackSize(int currentReturnValue, long startTime, Object nmsSlot, Object nmsItemStack) throws InvocationTargetException, IllegalAccessException {
        return (int) getSlotISMaxStackSize.invoke(null, currentReturnValue, startTime, nmsSlot, nmsItemStack);
    }

    public static int getBukkitMaterialMaxStackSize(int currentReturnValue, long startTime, Material material) throws InvocationTargetException, IllegalAccessException {
        return (int) getBukkitMaterialMaxStackSize.invoke(null, currentReturnValue, startTime, material);
    }

    public static int getBukkitItemStackMaxStackSize(int currentReturnValue, long startTime, ItemStack itemStack) throws InvocationTargetException, IllegalAccessException {
        return (int) getBukkitItemStackMaxStackSize.invoke(null, currentReturnValue, startTime, itemStack);
    }

    public static int getCraftBukkitItemStackMaxStackSize(int currentReturnValue, long startTime, ItemStack itemStack) throws InvocationTargetException, IllegalAccessException {
        return (int) getCraftBukkitItemStackMaxStackSize.invoke(null, currentReturnValue, startTime, itemStack);
    }
}
