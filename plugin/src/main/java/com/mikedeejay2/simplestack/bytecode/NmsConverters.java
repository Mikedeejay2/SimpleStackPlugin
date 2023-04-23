package com.mikedeejay2.simplestack.bytecode;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static com.mikedeejay2.simplestack.bytecode.MappingsLookup.lastNms;
import static com.mikedeejay2.simplestack.bytecode.MappingsLookup.nms;

/**
 * Converters for some NMS objects.
 * <p>
 * Benchmark of Reflection vs MethodHandle:
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
 *         <td>1.71ms</td>
 *         <td>1.87ms</td>
 *         <td>2.74ms</td>
 *         <td>10.32ms</td>
 *         <td>15.92ms</td>
 *     </tr>
 *     <tr>
 *         <td>Handle</td>
 *         <td>1.24ms</td>
 *         <td>1.40ms</td>
 *         <td>2.22ms</td>
 *         <td>6.94ms</td>
 *         <td>12.19ms</td>
 *     </tr>
 * </table>
 *
 * @author Mikedeejay2
 */
public final class NmsConverters {
    private static final MethodHandle HANDLE_AS_BUKKIT_COPY;
    private static final MethodHandle HANDLE_SLOT_SLOT;
    private static final MethodHandle HANDLE_SLOT_CONTAINER;
    private static final MethodHandle HANDLE_CRAFT_INVENTORY;

    // Get all NMS classes and methods using MappingsLookup
    static {
        try {
            final MethodHandles.Lookup lookup = MethodHandles.lookup();

            final Class<?> itemStackClass = nms("ItemStack").toClass();
            final Class<?> craftItemStackClass = nms("CraftItemStack").toClass();
            final Method methodAsBukkitCopy = craftItemStackClass.getMethod(
                lastNms().method("asBukkitCopy").name(), itemStackClass);
            methodAsBukkitCopy.setAccessible(true);
            HANDLE_AS_BUKKIT_COPY = lookup.unreflect(methodAsBukkitCopy);

            final Class<?> slotClass = nms("Slot").toClass();
            final Field fieldSlotSlot = slotClass.getDeclaredField(lastNms().field("slot").name());
            fieldSlotSlot.setAccessible(true);
            HANDLE_SLOT_SLOT = lookup.unreflectGetter(fieldSlotSlot);

            final Field fieldSlotContainer = slotClass.getDeclaredField(lastNms().field("container").name());
            fieldSlotContainer.setAccessible(true);
            HANDLE_SLOT_CONTAINER = lookup.unreflectGetter(fieldSlotContainer);

            final Class<?> iInventoryClass = nms("IInventory").toClass();
            final Class<?> craftInventoryClass = nms("CraftInventory").toClass();
            final Constructor<?> constructorCraftInventory = craftInventoryClass.getConstructor(iInventoryClass);
            constructorCraftInventory.setAccessible(true);
            HANDLE_CRAFT_INVENTORY = lookup.unreflectConstructor(constructorCraftInventory);
        } catch(NoSuchMethodException | NoSuchFieldException | IllegalAccessException e) {
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
            return (ItemStack) HANDLE_AS_BUKKIT_COPY.invoke(nmsItemStack);
        } catch(Throwable e) {
            Bukkit.getLogger().severe(String.format("SimpleStack could not convert ItemStack \"%s\"", nmsItemStack));
            e.printStackTrace();
            return null;
        }
    }

    public static int slotToSlot(Object nmsSlot) {
        try {
            return (int) HANDLE_SLOT_SLOT.invoke(nmsSlot);
        } catch(Throwable e) {
            Bukkit.getLogger().severe(String.format("SimpleStack could not convert slot \"%s\"", nmsSlot));
            e.printStackTrace();
            return -1;
        }
    }

    public static Inventory slotToInventory(Object nmsSlot) {
        try {
            Object nmsIInventory = HANDLE_SLOT_CONTAINER.invoke(nmsSlot);
            return (Inventory) HANDLE_CRAFT_INVENTORY.invoke(nmsIInventory);
        } catch(Throwable e) {
            Bukkit.getLogger().severe(String.format("SimpleStack could not convert slot \"%s\"", nmsSlot));
            e.printStackTrace();
            return null;
        }
    }
}
