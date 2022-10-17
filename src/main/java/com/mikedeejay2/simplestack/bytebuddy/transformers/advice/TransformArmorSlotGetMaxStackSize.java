package com.mikedeejay2.simplestack.bytebuddy.transformers.advice;

import com.mikedeejay2.simplestack.api.SimpleStackAPI;
import com.mikedeejay2.simplestack.api.event.ArmorSlotMaxAmountEvent;
import com.mikedeejay2.simplestack.api.event.SlotMaxAmountEvent;
import com.mikedeejay2.simplestack.bytebuddy.MappingsLookup;
import com.mikedeejay2.simplestack.bytebuddy.MethodVisitorInfo;
import com.mikedeejay2.simplestack.bytebuddy.Transformer;
import com.mikedeejay2.simplestack.debug.SimpleStackTimingsImpl;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.AsmVisitorWrapper;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;

import static com.mikedeejay2.simplestack.bytebuddy.MappingsLookup.*;

/**
 * Advice for changing the maximum stack size of armor slots. Used to specify whether armor is stackable or not when
 * worn.
 *
 * @author Mikedeejay2
 */
@Transformer({"1.19", "1.19.1"})
public class TransformArmorSlotGetMaxStackSize implements MethodVisitorInfo {
    private static final SimpleStackTimingsImpl TIMINGS = (SimpleStackTimingsImpl) SimpleStackAPI.getTimings();

    @Override
    public AsmVisitorWrapper.ForDeclaredMethods.MethodVisitorWrapper getWrapper() {
        return Advice.to(ArmorSlotAdvice.class);
    }

    @Override
    public MappingsLookup.MappingEntry getMappingEntry() {
        return nms("ArmorSlot").method("getMaxStackSize");
    }

    public static int getArmorSlotMaxStackSize(int currentReturnValue, long startTime, Object nmsSlot) {
        final Inventory inventory = NmsConverters.slotToInventory(nmsSlot);
        final int slot = NmsConverters.slotToSlot(nmsSlot);
        final SlotMaxAmountEvent slotEvent = new SlotMaxAmountEvent(inventory, slot, currentReturnValue);
        Bukkit.getPluginManager().callEvent(slotEvent);
        final ArmorSlotMaxAmountEvent armorSlotEvent = new ArmorSlotMaxAmountEvent(inventory, slot, slotEvent.getAmount());
        Bukkit.getPluginManager().callEvent(armorSlotEvent);
        TIMINGS.collect(startTime, "Armor slot redirect", true);
        return armorSlotEvent.getAmount();
    }

    public static class ArmorSlotAdvice {
        @Advice.OnMethodEnter
        public static long onMethodEnter() {
            return System.nanoTime();
        }

        @Advice.OnMethodExit
        public static void onMethodExit(@Advice.Return(readOnly = false) int returnValue, @Advice.Enter long startTime, @Advice.This Object nmsSlot) {
            try {
                Plugin plugin = Bukkit.getPluginManager().getPlugin("SimpleStack");
                ClassLoader pluginClassLoader = plugin.getClass().getClassLoader();
                Class<?> interceptClass = Class.forName("com.mikedeejay2.simplestack.bytebuddy.transformers.advice.TransformArmorSlotGetMaxStackSize", false, pluginClassLoader);
                Method maxStackSizeMethod = interceptClass.getMethod("getArmorSlotMaxStackSize", int.class, long.class, Object.class);
                returnValue = (int) maxStackSizeMethod.invoke(null, returnValue, startTime, nmsSlot);
            } catch(Throwable throwable) {
                Bukkit.getLogger().severe("Simple Stack encountered an exception while processing an armor slot");
                throwable.printStackTrace();
            }
        }
    }
}
