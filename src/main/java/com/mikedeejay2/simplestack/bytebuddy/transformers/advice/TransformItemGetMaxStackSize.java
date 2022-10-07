package com.mikedeejay2.simplestack.bytebuddy.transformers.advice;

import com.mikedeejay2.simplestack.MappingsLookup;
import com.mikedeejay2.simplestack.SimpleStack;
import com.mikedeejay2.simplestack.bytebuddy.MethodVisitorInfo;
import com.mikedeejay2.simplestack.bytebuddy.Transformer;
import com.mikedeejay2.simplestack.debug.DebugSystem;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.AsmVisitorWrapper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;

import static com.mikedeejay2.simplestack.MappingsLookup.*;

/**
 * Advice for changing the max stack size of an Item. This is a general item, not an ItemStack, similar to Material in
 * Bukkit. This is the max stack size used for the properties of an item.
 *
 * @author Mikedeejay2
 */
@Transformer({"1.19"})
public class TransformItemGetMaxStackSize implements MethodVisitorInfo {
    private static final DebugSystem DEBUG = SimpleStack.getInstance().getDebugSystem();

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
        String key = nmsItem.toString();
        key = key.toUpperCase(java.util.Locale.ENGLISH);
        Material material;
        try {
            material = Material.valueOf(key);
        } catch(IllegalArgumentException e) {
            throw new IllegalStateException(String.format("SimpleStack could not find material of ID \"%s\"", key));
        }
        int maxStackSize = SimpleStack.getInstance().config().getAmount(material);
        if(maxStackSize == -1) maxStackSize = currentReturnValue;
        DEBUG.collect(startTime, "Item size redirect", false);

        return maxStackSize;
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

        /**
         * <p>
         * Exit the <code>getMaxStackSize()</code> method. This method has to enter Simple Stack's ClassLoader to
         * access its classes, so reflection must be used in this method.
         * </p>
         * <p>
         * This method injects these instructions into <code>getMaxStackSize()</code>:
         * <ol>
         *     <li>
         *         Get the SimpleStack plugin from Bukkit's {@link org.bukkit.plugin.PluginManager#getPlugin(String)}.
         *         The Bukkit API is loaded on the same ClassLoader as Minecraft itself, so no worries in getting any
         *         of these classes. This {@link Plugin} object will be {@link SimpleStack} but it can't be casted to it
         *         because the <code>ClassLoader</code> that this code is operating in will not know where to find any
         *         SimpleStack classes <i>(Plugins are jar files and therefore require that they are loaded through
         *         their own <code>PluginClassLoader</code>; all of this happens in
         *         {@link org.bukkit.plugin.java.JavaPluginLoader})</i>.
         *     </li>
         *     <li>
         *         Get the {@link ClassLoader} of SimpleStack. This will be a <code>PluginClassLoader</code> and it's
         *         needed to locate and use any of the plugin's classes.
         *     </li>
         *     <li>
         *         Get the {@link TransformItemGetMaxStackSize} class using the obtained {@link ClassLoader}. This class is
         *         needed to get the redirect methods.
         *     </li>
         *     <li>
         *         Get the method {@link TransformItemGetMaxStackSize#getItemMaxStackSize(int, long, Object)}. This method
         *         will be run in the plugin's <code>ClassLoader</code> which means that it has access to any plugin
         *         method.
         *     </li>
         *     <li>
         *         Invoke the retrieved method and set the returnValue parameter to its return value. The returnValue
         *         parameter is used by Byte Buddy to change what is actually returned by the
         *         <code>getMaxStackSize()</code> method. The invoked method will get what Simple Stack says the max
         *         stack size should be.
         *     </li>
         * </ol>
         * All of this must occur explicitly within this method, no helper methods from the plugin can be used because
         * calling a plugin's method would be attempting to call a method that Minecraft's <code>ClassLoader</code>
         * would not be able to find, most likely causing a server crash.
         * </p>
         *
         * @param returnValue The original return value of the method. This argument is modified at the end of this
         *                    method which Byte Buddy uses to change what the <code>getMaxStackSize()</code> method
         *                    actually returns.
         * @param startTime   The starting nano time of the method, generated in {@link TransformItemGetMaxStackSize.ItemAdvice#onMethodEnter()},
         *                    used for debug purposes.
         * @param item        The NMS Item object that this method is operating within.
         * @throws Throwable  Any possible errors caused during reflective calls.
         */
        @Advice.OnMethodExit
        public static void onMethodExit(@Advice.Return(readOnly = false) int returnValue, @Advice.Enter long startTime, @Advice.This Object item) {
            try {
                Plugin plugin = Bukkit.getPluginManager().getPlugin("SimpleStack");
                ClassLoader pluginClassLoader = plugin.getClass().getClassLoader();
                Class<?> transformerClass = Class.forName("com.mikedeejay2.simplestack.bytebuddy.transformers.advice.TransformItemGetMaxStackSize", false, pluginClassLoader);
                Method maxStackSizeMethod = transformerClass.getMethod("getItemMaxStackSize", int.class, long.class, Object.class);
                returnValue = (int) maxStackSizeMethod.invoke(null, returnValue, startTime, item);
            } catch(Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }
}