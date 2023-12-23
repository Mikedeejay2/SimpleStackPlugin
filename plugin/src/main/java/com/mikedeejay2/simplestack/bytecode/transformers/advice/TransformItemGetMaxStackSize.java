package com.mikedeejay2.simplestack.bytecode.transformers.advice;

import com.mikedeejay2.simplestack.api.SimpleStackAPI;
import com.mikedeejay2.simplestack.api.event.MaterialMaxAmountEvent;
import com.mikedeejay2.simplestack.bytecode.*;
import com.mikedeejay2.simplestack.debug.SimpleStackTimingsImpl;
import com.mikedeejay2.simplestack.util.NmsConverters;
import com.mikedeejay2.simplestack.util.SafeEventCall;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.AsmVisitorWrapper;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import static com.mikedeejay2.simplestack.bytecode.MappingsLookup.*;

/**
 * Advice for changing the max stack size of an Item. This is a general item, not an ItemStack, similar to Material in
 * Bukkit. This is the max stack size used for the properties of an item.
 *
 * @author Mikedeejay2
 */
@Transformer({
    "1.20.4", "1.20.2", "1.20.1", "1.20",
    "1.19", "1.19.1", "1.19.2", "1.19.3", "1.19.4",
    "1.18", "1.18.1", "1.18.2"
})
public class TransformItemGetMaxStackSize implements MethodVisitorInfo {
    private static final SimpleStackTimingsImpl TIMINGS = (SimpleStackTimingsImpl) SimpleStackAPI.getTimings();

    @Override
    public AsmVisitorWrapper.ForDeclaredMethods.MethodVisitorWrapper getWrapper() {
        return Advice.to(ItemAdvice.class);
    }

    @Override
    public MappingsLookup.MappingEntry getMappingEntry() {
        return nms("Item").method("getMaxStackSize");
    }

    /**
     * Get the maximum stack size of a NMS <code>Item</code>. This gets the <code>toString()</code> of the
     * <code>Item</code> and converts it to a {@link Material} to get the item size from Simple Stack's config.
     * If the config returns <code>-1</code>, it will return the <code>currentReturnValue</code> which is the vanilla
     * max stack amount.
     *
     * @param currentReturnValue The original max stack amount from Minecraft's code
     * @param startTime The method's start time, used for debug purposes
     * @param nmsItem The NMS <code>Item</code>
     * @return The new stack size
     */
    public static int getItemMaxStackSize(int currentReturnValue, long startTime, Object nmsItem) {
        final Material material = NmsConverters.itemToMaterial(nmsItem);
        final MaterialMaxAmountEvent event = new MaterialMaxAmountEvent(material, currentReturnValue);
        SafeEventCall.callEvent(event);
        TIMINGS.collect(startTime, "Item size redirect", false);
        return event.getAmount();
    }

    /**
     * Advice class for <b>{@code net.minecraft.world.item.Item}</b>. The code in this class is copied over to the code
     * in <b>{@code net.minecraft.world.item.Item#getMaxStackSize()}</b> to redirect functionality of that method to
     * Simple Stack. The method that is called as a result of this advice is
     * {@link TransformItemGetMaxStackSize#getItemMaxStackSize(int, long, Object)}
     */
    public static class ItemAdvice {

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
            @Advice.This Object item) {
            try {
                returnValue = AdviceBridge.getItemMaxStackSize(returnValue, startTime, item);
            } catch(Throwable throwable) {
                Bukkit.getLogger().severe("Simple Stack encountered an exception while processing an Item");
                throwable.printStackTrace();
            }
        }
    }
}