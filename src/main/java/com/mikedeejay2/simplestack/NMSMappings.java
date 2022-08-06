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
            "f",
            "org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack",
            "asBukkitCopy",
            "a",
            "net.minecraft.world.ContainerUtil",
            "a",
            "o", "e", "g"))
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
        public final String classNameCraftItemStack;
        public final String methodNameCraftItemStackAsBukkitCopy;
        public final String methodNameItemStackSplit;
        public final String classNameContainerUtil;
        public final String methodNameContainerUtilRemoveItem;
        public final String methodNameItemStackCopy;
        public final String methodNameItemStackSetCount;
        public final String methodNameItemStackShrink;

        private Mappings(
            String classNameItem,
            String classNameItemStack,
            String methodNameItemGetMaxStackSize,
            String methodNameItemStackGetMaxStackSize,
            String classNameCraftItemStack,
            String methodNameCraftItemStackAsBukkitCopy,
            String methodNameItemStackSplit,
            String classNameContainerUtil,
            String methodNameContainerUtilRemoveItem,
            String methodNameItemStackCopy,
            String methodNameItemStackSetCount,
            String methodNameItemStackShrink) {
            this.classNameItem = classNameItem;
            this.classNameItemStack = classNameItemStack;
            this.methodNameItemGetMaxStackSize = methodNameItemGetMaxStackSize;
            this.methodNameItemStackGetMaxStackSize = methodNameItemStackGetMaxStackSize;
            this.classNameCraftItemStack = classNameCraftItemStack;
            this.methodNameCraftItemStackAsBukkitCopy = methodNameCraftItemStackAsBukkitCopy;
            this.methodNameItemStackSplit = methodNameItemStackSplit;
            this.classNameContainerUtil = classNameContainerUtil;
            this.methodNameContainerUtilRemoveItem = methodNameContainerUtilRemoveItem;
            this.methodNameItemStackCopy = methodNameItemStackCopy;
            this.methodNameItemStackSetCount = methodNameItemStackSetCount;
            this.methodNameItemStackShrink = methodNameItemStackShrink;
        }
    }
}
