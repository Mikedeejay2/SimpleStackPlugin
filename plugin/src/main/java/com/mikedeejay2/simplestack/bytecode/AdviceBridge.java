package com.mikedeejay2.simplestack.bytecode;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;

public final class AdviceBridge {
    public static final Method getArmorSlotMaxStackSize;
    public static final Method getItemMaxStackSize;
    public static final Method getItemStackMaxStackSize;
    public static final Method getSlotMaxStackSize;
    public static final Method getSlotISMaxStackSize;

    static {
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
        } catch(ClassNotFoundException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
