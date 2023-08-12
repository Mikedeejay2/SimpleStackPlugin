package com.mikedeejay2.simplestack.bytecode.transformers.advice;

import com.mikedeejay2.simplestack.api.SimpleStackAPI;
import com.mikedeejay2.simplestack.api.event.SlotMaxAmountEvent;
import com.mikedeejay2.simplestack.bytecode.AdviceBridge;
import com.mikedeejay2.simplestack.bytecode.MethodVisitorInfo;
import com.mikedeejay2.simplestack.bytecode.NmsConverters;
import com.mikedeejay2.simplestack.bytecode.Transformer;
import com.mikedeejay2.simplestack.debug.SimpleStackTimingsImpl;
import com.mikedeejay2.simplestack.util.SafeEventCall;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.AsmVisitorWrapper;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import static com.mikedeejay2.simplestack.bytecode.MappingsLookup.MappingEntry;
import static com.mikedeejay2.simplestack.bytecode.MappingsLookup.nms;

/**
 * Advice for changing specific slots of a container. (ItemStack parameterized)
 *
 * @author Mikedeejay2
 */
@Transformer({
    "1.20.1", "1.20",
    "1.19", "1.19.1", "1.19.2", "1.19.3", "1.19.4",
    "1.18", "1.18.1", "1.18.2"
})
public class TransformSlotISGetMaxStackSize implements MethodVisitorInfo {
    private static final SimpleStackTimingsImpl TIMINGS = (SimpleStackTimingsImpl) SimpleStackAPI.getTimings();

    @Override
    public AsmVisitorWrapper.ForDeclaredMethods.MethodVisitorWrapper getWrapper() {
        return Advice.to(SlotAdvice.class);
    }

    @Override
    public MappingEntry getMappingEntry() {
        return nms("Slot").method("getMaxStackSize1");
    }

    public static int getSlotMaxStackSize(int currentReturnValue, long startTime, Object nmsSlot, Object nmsItemStack) {
        if(SlotMaxAmountEvent.getHandlerList().getRegisteredListeners().length == 0) return currentReturnValue;
        final Inventory inventory = NmsConverters.slotToInventory(nmsSlot);
        final int slot = NmsConverters.slotToSlot(nmsSlot);
        final ItemStack itemStack = nmsItemStack != null ? NmsConverters.itemStackToItemStack(nmsItemStack) : null;
        final SlotMaxAmountEvent event = new SlotMaxAmountEvent(inventory, slot, currentReturnValue, itemStack);
        SafeEventCall.callEvent(event);
        TIMINGS.collect(startTime, "Slot (ItemStack) redirect", true);
        return event.getAmount();
    }

    public static class SlotAdvice {
        @Advice.OnMethodEnter
        public static long onMethodEnter() {
            return System.nanoTime();
        }

        @Advice.OnMethodExit
        public static void onMethodExit(
            @Advice.Return(readOnly = false) int returnValue,
            @Advice.Enter long startTime,
            @Advice.This Object nmsSlot,
            @Advice.Argument(0) Object nmsItemStack) {
            try {
                returnValue = AdviceBridge.getSlotISMaxStackSize(returnValue, startTime, nmsSlot, nmsItemStack);
            } catch(Throwable throwable) {
                Bukkit.getLogger().severe("Simple Stack encountered an exception while processing a slot");
                throwable.printStackTrace();
            }
        }
    }
}
