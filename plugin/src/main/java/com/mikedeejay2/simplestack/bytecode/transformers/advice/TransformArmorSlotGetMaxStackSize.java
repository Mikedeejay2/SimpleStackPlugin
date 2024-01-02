package com.mikedeejay2.simplestack.bytecode.transformers.advice;

import com.mikedeejay2.simplestack.api.SimpleStackAPI;
import com.mikedeejay2.simplestack.api.event.ArmorSlotMaxAmountEvent;
import com.mikedeejay2.simplestack.api.event.SlotMaxAmountEvent;
import com.mikedeejay2.simplestack.bytecode.*;
import com.mikedeejay2.simplestack.debug.SimpleStackTimingsImpl;
import com.mikedeejay2.simplestack.util.NmsConverters;
import com.mikedeejay2.simplestack.util.SafeEventCall;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.AsmVisitorWrapper;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import java.util.logging.Level;

import static com.mikedeejay2.simplestack.bytecode.MappingsLookup.*;

/**
 * Advice for changing the maximum stack size of armor slots. Used to specify whether armor is stackable or not when
 * worn.
 *
 * @author Mikedeejay2
 */
@Transformer("1.18-1.20.4")
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
        SafeEventCall.callEvent(slotEvent);
        final ArmorSlotMaxAmountEvent armorSlotEvent = new ArmorSlotMaxAmountEvent(inventory, slot, slotEvent.getAmount());
        SafeEventCall.callEvent(armorSlotEvent);
        TIMINGS.collect(startTime, "Armor slot redirect", true);
        return armorSlotEvent.getAmount();
    }

    public static class ArmorSlotAdvice {
        @Advice.OnMethodEnter
        public static long onMethodEnter() {
            return System.nanoTime();
        }

        @Advice.OnMethodExit
        public static void onMethodExit(
            @Advice.Return(readOnly = false) int returnValue,
            @Advice.Enter long startTime,
            @Advice.This Object nmsSlot) {
            try {
                returnValue = AdviceBridge.getArmorSlotMaxStackSize(returnValue, startTime, nmsSlot);
            } catch(Throwable throwable) {
                Bukkit.getLogger().log(Level.SEVERE, "Simple Stack encountered an exception while processing an armor slot", throwable);
            }
        }
    }
}
