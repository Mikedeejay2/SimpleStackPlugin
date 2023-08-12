package com.mikedeejay2.simplestack.bytecode.transformers.advice;

import com.mikedeejay2.simplestack.api.SimpleStackAPI;
import com.mikedeejay2.simplestack.api.event.ItemStackMaxAmountEvent;
import com.mikedeejay2.simplestack.bytecode.*;
import com.mikedeejay2.simplestack.debug.SimpleStackTimingsImpl;
import com.mikedeejay2.simplestack.util.SafeEventCall;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.AsmVisitorWrapper;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import static com.mikedeejay2.simplestack.bytecode.MappingsLookup.*;

/**
 * Advice for changing the max stack size of an ItemStack.
 *
 * @author Mikedeejay2
 */
@Transformer({
    "1.20.1", "1.20",
    "1.19", "1.19.1", "1.19.2", "1.19.3", "1.19.4",
    "1.18", "1.18.1", "1.18.2"
})
public class TransformItemStackGetMaxStackSize implements MethodVisitorInfo {
    private static final SimpleStackTimingsImpl TIMINGS = (SimpleStackTimingsImpl) SimpleStackAPI.getTimings();

    @Override
    public AsmVisitorWrapper.ForDeclaredMethods.MethodVisitorWrapper getWrapper() {
        return Advice.to(ItemStackAdvice.class);
    }

    @Override
    public MappingsLookup.MappingEntry getMappingEntry() {
        return nms("ItemStack").method("getMaxStackSize");
    }

    /**
     * Get the maximum stack size of a NMS <code>ItemStack</code>. This converts the NMS ItemStack into a Bukkit
     * ItemStack through a reflective call to
     * <b>{@code org.bukkit.craftbukkit.inventory.CraftItemStack#asBukkitCopy()}</b> which is then passed Simple Stack's
     * config to get the item size. If the config returns <code>-1</code>, it will return the
     * <code>currentReturnValue</code> which is the vanilla max stack amount.
     *
     * @param currentReturnValue The original max stack amount from Minecraft's code
     * @param startTime The method's start time, used for debug purposes
     * @param nmsItemStack The NMS <code>Item</code>
     * @return The new stack size
     */
    public static int getItemStackMaxStackSize(int currentReturnValue, long startTime, Object nmsItemStack) {
        final ItemStack itemStack = NmsConverters.itemStackToItemStack(nmsItemStack);
        final ItemStackMaxAmountEvent event = new ItemStackMaxAmountEvent(itemStack, currentReturnValue);
        SafeEventCall.callEvent(event);
        TIMINGS.collect(startTime, "ItemStack size redirect", true);
        return event.getAmount();
    }

    /**
     * Advice class for <b>{@code net.minecraft.world.item.ItemStack}</b>. The code in this class is copied over to the
     * code in <b>{@code net.minecraft.world.ItemStack.Item#getMaxStackSize()}</b> to redirect functionality of that
     * method to Simple Stack. The method that is called as a result of this advice is
     * {@link TransformItemStackGetMaxStackSize#getItemStackMaxStackSize(int, long, Object)}
     */
    public static class ItemStackAdvice {

        /**
         * @see TransformItemGetMaxStackSize.ItemAdvice#onMethodEnter()
         */
        @Advice.OnMethodEnter
        public static long onMethodEnter() {
            return System.nanoTime();
        }

        /**
         * @see TransformItemGetMaxStackSize.ItemAdvice#onMethodExit(int, long, Object)
         */
        @Advice.OnMethodExit
        public static void onMethodExit(
            @Advice.Return(readOnly = false) int returnValue,
            @Advice.Enter long startTime,
            @Advice.This Object itemStack) {
            try {
                returnValue = AdviceBridge.getItemStackMaxStackSize(returnValue, startTime, itemStack);
            } catch(Throwable throwable) {
                Bukkit.getLogger().severe("Simple Stack encountered an exception while processing an ItemStack");
                throwable.printStackTrace();
            }
        }
    }
}
