package com.mikedeejay2.simplestack.bytebuddy.transformers.advice;

import com.mikedeejay2.simplestack.SimpleStack;
import com.mikedeejay2.simplestack.bytebuddy.MethodVisitorInfo;
import com.mikedeejay2.simplestack.debug.DebugSystem;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.AsmVisitorWrapper;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;

import static com.mikedeejay2.simplestack.MappingsLookup.*;

public class TransformSlotGetMaxStackSize implements MethodVisitorInfo {
    private static final DebugSystem DEBUG = SimpleStack.getInstance().getDebugSystem();

    @Override
    public AsmVisitorWrapper.ForDeclaredMethods.MethodVisitorWrapper getWrapper() {
        return Advice.to(SlotAdvice.class);
    }

    @Override
    public MappingEntry getMappingEntry() {
        return nms("Slot").method("getMaxStackSize");
    }

    public static int getSlotMaxStackSize(int currentReturnValue, long startTime) {
        int slotStackAmount = currentReturnValue;
        DEBUG.collect(startTime, "Slot redirect", true);

        return slotStackAmount;
    }

    public static class SlotAdvice {
        @Advice.OnMethodEnter
        public static long onMethodEnter() {
            return System.nanoTime();
        }

        @Advice.OnMethodExit
        public static void onMethodExit(@Advice.Return(readOnly = false) int returnValue, @Advice.Enter long startTime) throws Throwable {
            Plugin plugin = Bukkit.getPluginManager().getPlugin("SimpleStack");
            ClassLoader pluginClassLoader = plugin.getClass().getClassLoader();
            Class<?> interceptClass = Class.forName("com.mikedeejay2.simplestack.bytebuddy.transformers.advice.TransformSlotGetMaxStackSize", false, pluginClassLoader);
            Method maxStackSizeMethod = interceptClass.getMethod("getSlotMaxStackSize", int.class, long.class);
            returnValue = (int) maxStackSizeMethod.invoke(null, returnValue, startTime);
        }
    }
}
