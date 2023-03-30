package com.mikedeejay2.simplestack.bytecode.transformers.advice;

import com.mikedeejay2.simplestack.api.SimpleStackAPI;
import com.mikedeejay2.simplestack.api.event.SlotMaxAmountEvent;
import com.mikedeejay2.simplestack.bytecode.MethodVisitorInfo;
import com.mikedeejay2.simplestack.bytecode.Transformer;
import com.mikedeejay2.simplestack.debug.SimpleStackTimingsImpl;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.AsmVisitorWrapper;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;

import static com.mikedeejay2.simplestack.bytecode.MappingsLookup.*;

/**
 * Advice for changing specific slots of a container.
 *
 * @author Mikedeejay2
 */
@Transformer({
    "1.19", "1.19.1", "1.19.2", "1.19.3",
    "1.18", "1.18.1", "1.18.2"
})
public class TransformSlotGetMaxStackSize implements MethodVisitorInfo {
    private static final SimpleStackTimingsImpl TIMINGS = (SimpleStackTimingsImpl) SimpleStackAPI.getTimings();

    @Override
    public AsmVisitorWrapper.ForDeclaredMethods.MethodVisitorWrapper getWrapper() {
        return Advice.to(SlotAdvice.class);
    }

    @Override
    public MappingEntry getMappingEntry() {
        return nms("Slot").method("getMaxStackSize");
    }

    public static int getSlotMaxStackSize(int currentReturnValue, long startTime, Object nmsSlot) {
        final Inventory inventory = NmsConverters.slotToInventory(nmsSlot);
        final int slot = NmsConverters.slotToSlot(nmsSlot);
        final SlotMaxAmountEvent event = new SlotMaxAmountEvent(inventory, slot, currentReturnValue);
        Bukkit.getPluginManager().callEvent(event);
        TIMINGS.collect(startTime, "Slot redirect", true);
        return event.getAmount();
    }

    public static class SlotAdvice {
        @Advice.OnMethodEnter
        public static long onMethodEnter() {
            return System.nanoTime();
        }

        @Advice.OnMethodExit
        public static void onMethodExit(@Advice.Return(readOnly = false) int returnValue, @Advice.Enter long startTime, @Advice.This Object nmsSlot) {
            try {
                Plugin plugin = Bukkit.getPluginManager().getPlugin("SimpleStack");
                ClassLoader pluginClassLoader = plugin.getClass().getClassLoader();
                Class<?> interceptClass = Class.forName("com.mikedeejay2.simplestack.bytecode.transformers.advice.TransformSlotGetMaxStackSize", false, pluginClassLoader);
                Method maxStackSizeMethod = interceptClass.getMethod("getSlotMaxStackSize", int.class, long.class, Object.class);
                returnValue = (int) maxStackSizeMethod.invoke(null, returnValue, startTime, nmsSlot);
            } catch(Throwable throwable) {
                Bukkit.getLogger().severe("Simple Stack encountered an exception while processing a slot");
                throwable.printStackTrace();
            }
        }
    }
}
