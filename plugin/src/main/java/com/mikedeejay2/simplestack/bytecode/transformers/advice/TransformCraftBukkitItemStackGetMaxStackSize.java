package com.mikedeejay2.simplestack.bytecode.transformers.advice;

import com.mikedeejay2.simplestack.api.SimpleStackAPI;
import com.mikedeejay2.simplestack.api.event.ItemStackMaxAmountEvent;
import com.mikedeejay2.simplestack.bytecode.AdviceBridge;
import com.mikedeejay2.simplestack.bytecode.MethodVisitorInfo;
import com.mikedeejay2.simplestack.bytecode.Transformer;
import com.mikedeejay2.simplestack.debug.SimpleStackTimingsImpl;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.AsmVisitorWrapper;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import static com.mikedeejay2.simplestack.bytecode.MappingsLookup.MappingEntry;
import static com.mikedeejay2.simplestack.bytecode.MappingsLookup.nms;

/**
 * Advice for changing the max stack size of an {@link ItemStack}. This does not interact with NMS in any way, it is
 * specifically for compatibility with other plugins.
 *
 * @author Mikedeejay2
 */
@Transformer({
    "1.20",
    "1.19", "1.19.1", "1.19.2", "1.19.3", "1.19.4",
    "1.18", "1.18.1", "1.18.2"
})
public class TransformCraftBukkitItemStackGetMaxStackSize implements MethodVisitorInfo {
    private static final SimpleStackTimingsImpl TIMINGS = (SimpleStackTimingsImpl) SimpleStackAPI.getTimings();

    @Override
    public AsmVisitorWrapper.ForDeclaredMethods.MethodVisitorWrapper getWrapper() {
        return Advice.to(ItemStackAdvice.class);
    }

    @Override
    public MappingEntry getMappingEntry() {
        return nms("CraftItemStack").method("getMaxStackSize");
    }

    public static int getCraftBukkitItemStackMaxStackSize(int currentReturnValue, long startTime, ItemStack itemStack) {
        final ItemStackMaxAmountEvent event = new ItemStackMaxAmountEvent(itemStack, currentReturnValue);
        Bukkit.getPluginManager().callEvent(event);
        TIMINGS.collect(startTime, "CraftBukkit ItemStack size redirect", true);
        return event.getAmount();
    }

    public static class ItemStackAdvice {

        /**
         * Enter the <code>getMaxStackSize()</code> method. The current system nano time is returned for debug purposes.
         *
         * @return The start time of this method
         */
        @Advice.OnMethodEnter
        public static long onMethodEnter() {
            return System.nanoTime();
        }

        @Advice.OnMethodExit
        public static void onMethodExit(
            @Advice.Return(readOnly = false) int returnValue,
            @Advice.Enter long startTime,
            @Advice.This ItemStack craftItemStack) {
            try {
                returnValue = AdviceBridge.getCraftBukkitItemStackMaxStackSize(returnValue, startTime, craftItemStack);
            } catch(Throwable throwable) {
                Bukkit.getLogger().severe("Simple Stack encountered an exception while processing a CraftBukkit ItemStack");
                throwable.printStackTrace();
            }
        }
    }
}