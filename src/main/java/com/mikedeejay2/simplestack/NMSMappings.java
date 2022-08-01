package com.mikedeejay2.simplestack;

import com.google.common.collect.ImmutableMap;
import com.mikedeejay2.mikedeejay2lib.util.version.MinecraftVersion;

import java.util.Map;

public final class NMSMappings {
    private static final Map<Integer, Mappings> NMS_MAPPINGS = new ImmutableMap.Builder<Integer, Mappings>()
        .put(19, new Mappings(
            "net.minecraft.world.item.Item",
            "net.minecraft.world.item.ItemStack",
            "m",
            "f"))
        .build();

    private static final Mappings currentMappings = NMS_MAPPINGS.get(MinecraftVersion.getVersionShort());

    public static boolean hasMappings() {
        return currentMappings != null;
    }

    public static Mappings get() {
        return currentMappings;
    }

    public static final class Mappings {
        public final String classNameItem;
        public final String classNameItemStack;
        public final String methodNameItemGetMaxStackSize;
        public final String methodNameItemStackGetMaxStackSize;

        private Mappings(
            String classNameItem,
            String classNameItemStack,
            String methodNameItemGetMaxStackSize,
            String methodNameItemStackGetMaxStackSize) {
            this.classNameItem = classNameItem;
            this.classNameItemStack = classNameItemStack;
            this.methodNameItemGetMaxStackSize = methodNameItemGetMaxStackSize;
            this.methodNameItemStackGetMaxStackSize = methodNameItemStackGetMaxStackSize;
        }
    }
}
