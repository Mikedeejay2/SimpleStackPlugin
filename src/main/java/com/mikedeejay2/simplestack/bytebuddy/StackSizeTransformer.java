package com.mikedeejay2.simplestack.bytebuddy;

import com.mikedeejay2.simplestack.NMSMappings;
import com.mikedeejay2.simplestack.SimpleStack;
import com.mikedeejay2.simplestack.config.DebugConfig;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.ResettableClassFileTransformer;
import net.bytebuddy.asm.Advice;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * <p>
 * Static class to redirect {@code getMaxStackSize()} methods to Simple Stack.
 * </p>
 * <p>
 * These methods (as of 1.19) are
 * <ul>
 *     <li><b>{@code net.minecraft.world.item.Item#getMaxStackSize()}</b></li>
 *     <li><b>{@code net.minecraft.world.item.ItemStack#getMaxStackSize()}</b></li>
 * </ul>
 * </p>
 * <p>
 * This class uses Byte Buddy to inject custom code into the two above methods to change their functionality. Instead
 * of returning the <code>maxStackSize</code> found in <code>net.minecraft.world.item.Item</code>, either
 * {@link StackSizeTransformer#getItemMaxStackSize(int, long, Object)} or
 * {@link StackSizeTransformer#getItemStackMaxStackSize(int, long, Object)} will be called to retrieve the new return
 * value from Simple Stack's configuration file.
 * </p>
 *
 * @author Mikedeejay2
 * @since 2.0.0
 */
public final class StackSizeTransformer {
    private static final Class<?> CLASS_ITEM;               // net.minecraft.world.item.Item
    private static final Class<?> CLASS_ITEM_STACK;         // net.minecraft.world.item.ItemStack
    private static final Class<?> CLASS_CRAFT_ITEM_STACK;   // org.bukkit.craftbukkit.inventory.CraftItemStack
    private static final Method METHOD_AS_BUKKIT_COPY;      // org.bukkit.craftbukkit.inventory.CraftItemStack#asBukkitCopy()

    private static ResettableClassFileTransformer transformer;

    // Get all NMS classes and methods using NMSMappings
    static {
        try {
            CLASS_ITEM = Class.forName(NMSMappings.get().classNameItem);
            CLASS_ITEM_STACK = Class.forName(NMSMappings.get().classNameItemStack);
            CLASS_CRAFT_ITEM_STACK = Class.forName(NMSMappings.get().classNameCraftItemStack);
            METHOD_AS_BUKKIT_COPY = CLASS_CRAFT_ITEM_STACK.getMethod(
                NMSMappings.get().methodNameCraftItemStackAsBukkitCopy, CLASS_ITEM_STACK);
        } catch(ClassNotFoundException | NoSuchMethodException e) {
            Bukkit.getLogger().severe("SimpleStack cannot locate NMS classes");
            throw new RuntimeException(e);
        }
    }

    /**
     * Install Byte Buddy agents to transform the following methods
     * <ul>
     *     <li><b>{@code net.minecraft.world.item.Item#getMaxStackSize()}</b></li>
     *     <li><b>{@code net.minecraft.world.item.ItemStack#getMaxStackSize()}</b></li>
     * </ul>
     * These methods are transformed to redirect to either
     * {@link StackSizeTransformer#getItemMaxStackSize(int, long, Object)} or
     * {@link StackSizeTransformer#getItemStackMaxStackSize(int, long, Object)} depending on which transformed method
     * is being called.
     */
    public static void install() {
        // AgentBuilder for net.minecraft.world.item.Item and net.minecraft.world.item.ItemStack
        transformer = new AgentBuilder.Default()
            .disableClassFormatChanges()
            .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION) // Use retransformation strategy to modify existing NMS classes
            .type(is(CLASS_ITEM)) // Match the Item class
            .transform(((builder, typeDescription, classLoader, module) -> builder.visit(
                Advice.to(ItemAdvice.class)
                    .on(named(NMSMappings.get().methodNameItemGetMaxStackSize)
                            .and(returns(int.class))
                            .and(takesNoArguments()))))) // Inject ItemAdvice into getMaxStackSize() method
            .type(is(CLASS_ITEM_STACK)) // Match the ItemStack class
            .transform(((builder, typeDescription, classLoader, module) -> builder.visit(
                Advice.to(ItemStackAdvice.class)
                    .on(named(NMSMappings.get().methodNameItemStackGetMaxStackSize)
                            .and(returns(int.class))
                            .and(takesNoArguments()))))) // Inject ItemStackAdvice into getMaxStackSize() method
            .installOnByteBuddyAgent(); // Inject

//        // AgentBuilder for net.minecraft.world.item.ItemStack
//        new AgentBuilder.Default()
//            .disableClassFormatChanges()
//            .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION) // Use retransformation strategy to modify existing NMS classes
//            .type(is(CLASS_ITEM_STACK)) // Match the ItemStack class
//            .transform(((builder, typeDescription, classLoader, module) -> builder.visit(
//                Advice.to(ItemStackAdvice.class)
//                    .on(named(NMSMappings.get().methodNameItemStackGetMaxStackSize)
//                            .and(returns(int.class))
//                            .and(takesNoArguments()))))) // Inject ItemStackAdvice into getMaxStackSize() method
//            .installOnByteBuddyAgent(); // Inject
    }

    public static void reset() {
        if(transformer != null) {
            transformer.reset(ByteBuddyHolder.getInstrumentation(), AgentBuilder.RedefinitionStrategy.RETRANSFORMATION);
            ByteBuddyHolder.getInstrumentation().removeTransformer(transformer);
        }
    }

    /**
     * Advice class for <b>{@code net.minecraft.world.item.Item}</b>. The code in this class is copied over to the code
     * in <b>{@code net.minecraft.world.item.Item#getMaxStackSize()}</b> to redirect functionality of that method to
     * Simple Stack. The method that is called as a result of this advice is
     * {@link StackSizeTransformer#getItemMaxStackSize(int, long, Object)}
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
         *         Get the {@link ClassLoader} of SimpleStack. This will be a <code>PluginClassLoader</code> and its
         *         needed to locate and use any of the plugin's classes.
         *     </li>
         *     <li>
         *         Get the {@link StackSizeTransformer} class using the obtained {@link ClassLoader}. This class is
         *         needed to get the redirect methods.
         *     </li>
         *     <li>
         *         Get the method {@link StackSizeTransformer#getItemMaxStackSize(int, long, Object)}. This method
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
         * @param startTime   The starting nano time of the method, generated in {@link ItemAdvice#onMethodEnter()},
         *                    used for debug purposes.
         * @param item        The NMS Item object that this method is operating within.
         * @throws Throwable  Any possible errors caused during reflective calls.
         */
        @Advice.OnMethodExit
        public static void onMethodExit(@Advice.Return(readOnly = false) int returnValue, @Advice.Enter long startTime, @Advice.This Object item) throws Throwable {
            Plugin plugin = Bukkit.getPluginManager().getPlugin("SimpleStack");
            ClassLoader pluginClassLoader = plugin.getClass().getClassLoader();
            Class<?> transformerClass = Class.forName("com.mikedeejay2.simplestack.bytebuddy.StackSizeTransformer", false, pluginClassLoader);
            Method maxStackSizeMethod = transformerClass.getMethod("getItemMaxStackSize", int.class, long.class, Object.class);
            returnValue = (int) maxStackSizeMethod.invoke(null, returnValue, startTime, item);
        }
    }

    /**
     * Advice class for <b>{@code net.minecraft.world.item.ItemStack}</b>. The code in this class is copied over to the
     * code in <b>{@code net.minecraft.world.ItemStack.Item#getMaxStackSize()}</b> to redirect functionality of that
     * method to Simple Stack. The method that is called as a result of this advice is
     * {@link StackSizeTransformer#getItemStackMaxStackSize(int, long, Object)}
     */
    public static class ItemStackAdvice {

        /**
         * @see ItemAdvice#onMethodEnter()
         */
        @Advice.OnMethodEnter
        public static long onMethodEnter() {
            return System.nanoTime();
        }

        /**
         * @see StackSizeTransformer.ItemAdvice#onMethodExit(int, long, Object)
         */
        @Advice.OnMethodExit
        public static void onMethodExit(@Advice.Return(readOnly = false) int returnValue, @Advice.Enter long startTime, @Advice.This Object itemStack) throws Throwable {
            Plugin plugin = Bukkit.getPluginManager().getPlugin("SimpleStack");
            ClassLoader pluginClassLoader = plugin.getClass().getClassLoader();
            Class<?> interceptClass = Class.forName("com.mikedeejay2.simplestack.bytebuddy.StackSizeTransformer", false, pluginClassLoader);
            Method maxStackSizeMethod = interceptClass.getMethod("getItemStackMaxStackSize", int.class, long.class, Object.class);
            returnValue = (int) maxStackSizeMethod.invoke(null, returnValue, startTime, itemStack);
        }
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
        printTimings(startTime);

        return maxStackSize;
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
        printTimings(startTime);

        return maxStackSize;
    }

    /**
     * Given that {@link DebugConfig#isPrintTimings()} is true, print the amount of time that the full
     * <code>getMaxStackSize()</code> method took to run in the server console.
     *
     * @param startTime The start time of the method
     */
    private static void printTimings(long startTime) {
        if(!SimpleStack.getInstance().getDebugConfig().isPrintTimings()) return;
        long endTime = System.nanoTime();
        SimpleStack.getInstance().sendInfo(String.format(
            "Item size redirect took %.4f milliseconds to complete",
            ((endTime - startTime) / 1000000.0)));
    }
}
