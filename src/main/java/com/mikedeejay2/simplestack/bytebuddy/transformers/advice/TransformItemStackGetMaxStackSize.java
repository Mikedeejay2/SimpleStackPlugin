package com.mikedeejay2.simplestack.bytebuddy.transformers.advice;

import com.mikedeejay2.simplestack.MappingsLookup;
import com.mikedeejay2.simplestack.SimpleStack;
import com.mikedeejay2.simplestack.bytebuddy.MethodVisitorInfo;
import com.mikedeejay2.simplestack.bytebuddy.Transformer;
import com.mikedeejay2.simplestack.debug.DebugSystem;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.AsmVisitorWrapper;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.mikedeejay2.simplestack.MappingsLookup.*;

/**
 * Advice for changing the max stack size of an ItemStack.
 *
 * @author Mikedeejay2
 */
@Transformer({"1.19"})
public class TransformItemStackGetMaxStackSize implements MethodVisitorInfo {
    private static final Method METHOD_AS_BUKKIT_COPY;      // org.bukkit.craftbukkit.inventory.CraftItemStack#asBukkitCopy()
    private static final DebugSystem DEBUG = SimpleStack.getInstance().getDebugSystem();

    // Get all NMS classes and methods using NMSMappings
    static {
        try {
            Class<?> itemStackClass = nms("ItemStack").toClass();
            Class<?> craftItemStackClass = nms("CraftItemStack").toClass();
            METHOD_AS_BUKKIT_COPY = craftItemStackClass.getMethod(
                lastNms().method("asBukkitCopy").name(), itemStackClass);
        } catch(NoSuchMethodException e) {
            Bukkit.getLogger().severe("SimpleStack cannot locate NMS classes");
            throw new RuntimeException(e);
        }
    }

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
    public static int getItemStackMaxStackSize(int currentReturnValue, long startTime, Object nmsItemStack) throws InvocationTargetException, IllegalAccessException {
        ItemStack itemStack = (ItemStack) METHOD_AS_BUKKIT_COPY.invoke(null, nmsItemStack);
        int maxStackSize = SimpleStack.getInstance().config().getAmount(itemStack);
        if(maxStackSize == -1) maxStackSize = currentReturnValue;
        DEBUG.collect(startTime, "ItemStack size redirect", true);

        return maxStackSize;
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
        public static void onMethodExit(@Advice.Return(readOnly = false) int returnValue, @Advice.Enter long startTime, @Advice.This Object itemStack) throws Throwable {
            Plugin plugin = Bukkit.getPluginManager().getPlugin("SimpleStack");
            ClassLoader pluginClassLoader = plugin.getClass().getClassLoader();
            Class<?> interceptClass = Class.forName("com.mikedeejay2.simplestack.bytebuddy.transformers.advice.TransformItemStackGetMaxStackSize", false, pluginClassLoader);
            Method maxStackSizeMethod = interceptClass.getMethod("getItemStackMaxStackSize", int.class, long.class, Object.class);
            returnValue = (int) maxStackSizeMethod.invoke(null, returnValue, startTime, itemStack);
        }
    }
}
