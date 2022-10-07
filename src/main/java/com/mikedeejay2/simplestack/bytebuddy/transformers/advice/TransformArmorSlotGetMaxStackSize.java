package com.mikedeejay2.simplestack.bytebuddy.transformers.advice;

import com.mikedeejay2.simplestack.MappingsLookup;
import com.mikedeejay2.simplestack.SimpleStack;
import com.mikedeejay2.simplestack.bytebuddy.MethodVisitorInfo;
import com.mikedeejay2.simplestack.bytebuddy.Transformer;
import com.mikedeejay2.simplestack.debug.DebugSystem;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.AsmVisitorWrapper;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;

import static com.mikedeejay2.simplestack.MappingsLookup.*;

/**
 * Advice for changing the maximum stack size of armor slots. Used to specify whether armor is stackable or not when
 * worn.
 *
 * @author Mikedeejay2
 */
@Transformer({"1.19"})
public class TransformArmorSlotGetMaxStackSize implements MethodVisitorInfo {
    private static final DebugSystem DEBUG = SimpleStack.getInstance().getDebugSystem();

    @Override
    public AsmVisitorWrapper.ForDeclaredMethods.MethodVisitorWrapper getWrapper() {
        return Advice.to(ArmorSlotAdvice.class);
    }

    @Override
    public MappingsLookup.MappingEntry getMappingEntry() {
        return nms("ArmorSlot").method("getMaxStackSize");
    }

    public static int getArmorSlotMaxStackSize(int currentReturnValue, long startTime) {
        boolean shouldStackArmor = SimpleStack.getInstance().config().isStackedArmorWearable();
        int armorStackAmount = shouldStackArmor ? SimpleStack.getInstance().config().getMaxAmount() : currentReturnValue;
        DEBUG.collect(startTime, "Armor slot redirect", true);

        return armorStackAmount;
    }

    public static class ArmorSlotAdvice {
        @Advice.OnMethodEnter
        public static long onMethodEnter() {
            return System.nanoTime();
        }

        @Advice.OnMethodExit
        public static void onMethodExit(@Advice.Return(readOnly = false) int returnValue, @Advice.Enter long startTime) throws Throwable {
            Plugin plugin = Bukkit.getPluginManager().getPlugin("SimpleStack");
            ClassLoader pluginClassLoader = plugin.getClass().getClassLoader();
            Class<?> interceptClass = Class.forName("com.mikedeejay2.simplestack.bytebuddy.transformers.advice.TransformArmorSlotGetMaxStackSize", false, pluginClassLoader);
            Method maxStackSizeMethod = interceptClass.getMethod("getArmorSlotMaxStackSize", int.class, long.class);
            returnValue = (int) maxStackSizeMethod.invoke(null, returnValue, startTime);
        }
    }
}
