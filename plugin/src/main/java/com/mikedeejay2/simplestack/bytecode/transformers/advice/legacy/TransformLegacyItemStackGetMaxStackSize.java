package com.mikedeejay2.simplestack.bytecode.transformers.advice.legacy;

import com.mikedeejay2.simplestack.api.SimpleStackAPI;
import com.mikedeejay2.simplestack.api.event.ItemStackMaxAmountEvent;
import com.mikedeejay2.simplestack.bytecode.AdviceBridge;
import com.mikedeejay2.simplestack.bytecode.MethodVisitorInfo;
import com.mikedeejay2.simplestack.bytecode.Transformer;
import com.mikedeejay2.simplestack.debug.SimpleStackTimingsImpl;
import com.mikedeejay2.simplestack.util.NmsConverters;
import com.mikedeejay2.simplestack.util.SafeEventCall;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.AsmVisitorWrapper;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.logging.Level;

import static com.mikedeejay2.simplestack.bytecode.MappingsLookup.MappingEntry;
import static com.mikedeejay2.simplestack.bytecode.MappingsLookup.nms;

/**
 * Advice for changing the max stack size of an ItemStack.
 *
 * @author Mikedeejay2
 */
@Transformer("1.18-1.20.4")
public class TransformLegacyItemStackGetMaxStackSize implements MethodVisitorInfo {
    private static final SimpleStackTimingsImpl TIMINGS = (SimpleStackTimingsImpl) SimpleStackAPI.getTimings();

    @Override
    public AsmVisitorWrapper.ForDeclaredMethods.MethodVisitorWrapper getWrapper() {
        return Advice.to(ItemStackAdvice.class);
    }

    @Override
    public MappingEntry getMappingEntry() {
        return nms("ItemStack").method("getMaxStackSize");
    }

    public static int getItemStackMaxStackSize(int currentReturnValue, long startTime, Object nmsItemStack) {
        final ItemStack itemStack = NmsConverters.itemStackToItemStack(nmsItemStack);
        final ItemStackMaxAmountEvent event = new ItemStackMaxAmountEvent(itemStack, currentReturnValue);
        SafeEventCall.callEvent(event);
        TIMINGS.collect(startTime, "ItemStack size redirect", true);
        return event.getAmount();
    }

    public static class ItemStackAdvice {
        @Advice.OnMethodEnter
        public static long onMethodEnter() {
            return System.nanoTime();
        }

        @Advice.OnMethodExit
        public static void onMethodExit(
            @Advice.Return(readOnly = false) int returnValue,
            @Advice.Enter long startTime,
            @Advice.This Object itemStack) {
            try {
                returnValue = AdviceBridge.getItemStackMaxStackSize(returnValue, startTime, itemStack);
            } catch(Throwable throwable) {
                Bukkit.getLogger().log(Level.SEVERE, "Simple Stack encountered an exception while processing an ItemStack", throwable);
            }
        }
    }
}
