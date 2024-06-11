package com.mikedeejay2.simplestack.util;

import org.bukkit.Bukkit;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Level;

import static com.mikedeejay2.simplestack.mappings.MappingsLookup.*;

public final class NmsComponentHandler {
    public static final MethodHandle HANDLE_STACK_COMPONENTS;
    public static final MethodHandle HANDLE_PATCHED_COMPONENTS_SET;
    public static final MethodHandle HANDLE_PATCHED_COMPONENTS_REMOVE;
    public static final Object MAX_STACK_SIZE_COMPONENT;

    static {
        try {
            final MethodHandles.Lookup lookup = MethodHandles.lookup();

            final Class<?> itemStackClass = nms("ItemStack").toClass();
            final Field fieldStackComponents = itemStackClass.getDeclaredField(nms("ItemStack").field("components").name());
            fieldStackComponents.setAccessible(true);
            HANDLE_STACK_COMPONENTS = lookup.unreflectGetter(fieldStackComponents);

            final Class<?> patchedDataComponentMapClass = nms("PatchedDataComponentMap").toClass();
            final Method patchedComponentsSetMethod = patchedDataComponentMapClass.getMethod(
                nms("PatchedDataComponentMap").method("set").name(),
                nms("DataComponentType").toClass(),
                Object.class
            );
            patchedComponentsSetMethod.setAccessible(true);
            HANDLE_PATCHED_COMPONENTS_SET = lookup.unreflect(patchedComponentsSetMethod);

            final Method patchedComponentsRemoveMethod = patchedDataComponentMapClass.getMethod(
                nms("PatchedDataComponentMap").method("remove").name(),
                nms("DataComponentType").toClass()
            );
            patchedComponentsRemoveMethod.setAccessible(true);
            HANDLE_PATCHED_COMPONENTS_REMOVE = lookup.unreflect(patchedComponentsRemoveMethod);

            final Class<?> dataComponentsClass = nms("DataComponents").toClass();
            final Field maxStackSizeComponentField = dataComponentsClass.getField(lastNms().field("MAX_STACK_SIZE").name());
            maxStackSizeComponentField.setAccessible(true);
            MAX_STACK_SIZE_COMPONENT = maxStackSizeComponentField.get(null);
        } catch(NoSuchFieldException | IllegalAccessException | NoSuchMethodException e) {
            Bukkit.getLogger().severe("SimpleStack cannot locate NMS classes");
            throw new RuntimeException(e);
        }
    }

    public static Object getItemStackComponents(Object nmsItemStack) {
        try {
            return (Object) HANDLE_STACK_COMPONENTS.invoke(nmsItemStack);
        } catch(Throwable e) {
            Bukkit.getLogger().log(Level.SEVERE, String.format("SimpleStack could not get ItemStack components \"%s\"", nmsItemStack), e);
            return null;
        }
    }

    public static void setMaxStackSize(Object nmsItemStack, int stackSize) {
        try {
            final Object nmsComponents = getItemStackComponents(nmsItemStack);
            HANDLE_PATCHED_COMPONENTS_SET.invoke(nmsComponents, MAX_STACK_SIZE_COMPONENT, stackSize);
        } catch(Throwable e) {
            Bukkit.getLogger().log(Level.SEVERE, String.format("SimpleStack could not set ItemStack max stack size component \"%s\"", nmsItemStack), e);
        }
    }

    public static void removeMaxStackSize(Object nmsItemStack) {
        try {
            final Object nmsComponents = getItemStackComponents(nmsItemStack);
            HANDLE_PATCHED_COMPONENTS_REMOVE.invoke(nmsComponents, MAX_STACK_SIZE_COMPONENT);
        } catch(Throwable e) {
            Bukkit.getLogger().log(Level.SEVERE, String.format("SimpleStack could not remove ItemStack max stack size component \"%s\"", nmsItemStack), e);
        }
    }
}
