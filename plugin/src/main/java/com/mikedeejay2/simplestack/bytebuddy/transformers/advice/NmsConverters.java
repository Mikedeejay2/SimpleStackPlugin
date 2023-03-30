package com.mikedeejay2.simplestack.bytebuddy.transformers.advice;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.mikedeejay2.simplestack.bytebuddy.MappingsLookup.lastNms;
import static com.mikedeejay2.simplestack.bytebuddy.MappingsLookup.nms;

public final class NmsConverters {
    private static final Method METHOD_AS_BUKKIT_COPY;      // org.bukkit.craftbukkit.inventory.CraftItemStack#asBukkitCopy()
    private static final Field FIELD_SLOT_SLOT;
    private static final Field FIELD_SLOT_CONTAINER;
    private static final Constructor<?> CONSTRUCTOR_CRAFT_INVENTORY;

    // Get all NMS classes and methods using NMSMappings
    static {
        try {
            final Class<?> itemStackClass = nms("ItemStack").toClass();
            final Class<?> craftItemStackClass = nms("CraftItemStack").toClass();
            METHOD_AS_BUKKIT_COPY = craftItemStackClass.getMethod(
                lastNms().method("asBukkitCopy").name(), itemStackClass);

            final Class<?> slotClass = nms("Slot").toClass();
            FIELD_SLOT_SLOT = slotClass.getDeclaredField(lastNms().field("slot").name());
            FIELD_SLOT_CONTAINER = slotClass.getDeclaredField(lastNms().field("container").name());

            final Class<?> iInventoryClass = nms("IInventory").toClass();
            final Class<?> craftInventoryClass = nms("CraftInventory").toClass();
            CONSTRUCTOR_CRAFT_INVENTORY = craftInventoryClass.getConstructor(iInventoryClass);
        } catch(NoSuchMethodException | NoSuchFieldException e) {
            Bukkit.getLogger().severe("SimpleStack cannot locate NMS classes");
            throw new RuntimeException(e);
        }
    }

    public static Material itemToMaterial(Object nmsItem) {
        String key = nmsItem.toString();
        key = key.toUpperCase(java.util.Locale.ENGLISH);
        Material material;
        try {
            material = Material.valueOf(key);
        } catch(IllegalArgumentException e) {
            Bukkit.getLogger().severe(String.format("SimpleStack could not find material of ID \"%s\"", key));
            e.printStackTrace();
            return null;
        }
        return material;
    }

    public static ItemStack itemStackToItemStack(Object nmsItemStack) {
        try {
            return (ItemStack) METHOD_AS_BUKKIT_COPY.invoke(null, nmsItemStack);
        } catch(IllegalAccessException | InvocationTargetException e) {
            Bukkit.getLogger().severe(String.format("SimpleStack could not convert ItemStack \"%s\"", nmsItemStack));
            e.printStackTrace();
            return null;
        }
    }

    public static int slotToSlot(Object nmsSlot) {
        try {
            return (int) FIELD_SLOT_SLOT.get(nmsSlot);
        } catch(IllegalAccessException e) {
            Bukkit.getLogger().severe(String.format("SimpleStack could not convert slot \"%s\"", nmsSlot));
            e.printStackTrace();
            return -1;
        }
    }

    public static Inventory slotToInventory(Object nmsSlot) {
        try {
            Object nmsIInventory = FIELD_SLOT_CONTAINER.get(nmsSlot);
            return (Inventory) CONSTRUCTOR_CRAFT_INVENTORY.newInstance(nmsIInventory);
        } catch(IllegalAccessException | InstantiationException | InvocationTargetException e) {
            Bukkit.getLogger().severe(String.format("SimpleStack could not convert slot \"%s\"", nmsSlot));
            e.printStackTrace();
            return null;
        }
    }
}
