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

    public static void initialize() {
        final Plugin plugin = Bukkit.getPluginManager().getPlugin("SimpleStack");
        final ClassLoader pluginClassLoader = plugin.getClass().getClassLoader();
        try {
            getArmorSlotMaxStackSize = Class.forName("com.mikedeejay2.simplestack.bytecode.transformers.advice.TransformArmorSlotGetMaxStackSize", false, pluginClassLoader)
                .getMethod("getArmorSlotMaxStackSize", int.class, long.class, Object.class);
            getArmorSlotMaxStackSize.setAccessible(true);

            getItemMaxStackSize = Class.forName("com.mikedeejay2.simplestack.bytecode.transformers.advice.TransformItemGetMaxStackSize", false, pluginClassLoader)
                .getMethod("getItemMaxStackSize", int.class, long.class, Object.class);
            getItemMaxStackSize.setAccessible(true);

            getItemStackMaxStackSize = Class.forName("com.mikedeejay2.simplestack.bytecode.transformers.advice.TransformItemStackGetMaxStackSize", false, pluginClassLoader)
                .getMethod("getItemStackMaxStackSize", int.class, long.class, Object.class);
            getItemStackMaxStackSize.setAccessible(true);

            getSlotMaxStackSize = Class.forName("com.mikedeejay2.simplestack.bytecode.transformers.advice.TransformSlotGetMaxStackSize", false, pluginClassLoader)
                .getMethod("getSlotMaxStackSize", int.class, long.class, Object.class);
            getSlotMaxStackSize.setAccessible(true);

            getSlotISMaxStackSize = Class.forName("com.mikedeejay2.simplestack.bytecode.transformers.advice.TransformSlotISGetMaxStackSize", false, pluginClassLoader)
                .getMethod("getSlotMaxStackSize", int.class, long.class, Object.class, Object.class);
            getSlotISMaxStackSize.setAccessible(true);

            getBukkitMaterialMaxStackSize = Class.forName("com.mikedeejay2.simplestack.bytecode.transformers.advice.TransformBukkitMaterialGetMaxStackSize", false, pluginClassLoader)
                .getMethod("getBukkitMaterialMaxStackSize", int.class, long.class, Material.class);
            getBukkitMaterialMaxStackSize.setAccessible(true);

            getBukkitItemStackMaxStackSize = Class.forName("com.mikedeejay2.simplestack.bytecode.transformers.advice.TransformBukkitItemStackGetMaxStackSize", false, pluginClassLoader)
                .getMethod("getBukkitItemStackMaxStackSize", int.class, long.class, ItemStack.class);
            getBukkitItemStackMaxStackSize.setAccessible(true);
        } catch(ClassNotFoundException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
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
}
