package com.mikedeejay2.simplestack.bytebuddy;

import com.mikedeejay2.simplestack.NMSMappings;
import com.mikedeejay2.simplestack.SimpleStack;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;

import static net.bytebuddy.matcher.ElementMatchers.*;

public final class StackSizeTransformer {
    private static final Class<?> CLASS_ITEM;
    private static final Class<?> CLASS_ITEM_STACK;

    static {
        try {
            CLASS_ITEM = Class.forName(NMSMappings.get().classNameItem);
            CLASS_ITEM_STACK = Class.forName(NMSMappings.get().classNameItemStack);
        } catch(ClassNotFoundException e) {
            Bukkit.getLogger().severe("SimpleStack cannot locate NMS classes");
            throw new RuntimeException(e);
        }
    }

    public static void inject() {
        new AgentBuilder.Default()
            .disableClassFormatChanges()
            .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
            .type(is(CLASS_ITEM))
            .transform(((builder, typeDescription, classLoader, module) -> builder.visit(
                Advice.to(ItemAdvice.class)
                    .on(named(NMSMappings.get().methodNameItemGetMaxStackSize)
                            .and(returns(int.class))
                            .and(takesNoArguments())))))
            .installOnByteBuddyAgent();




//        BYTE_BUDDY
//            .with(Implementation.Context.Disabled.Factory.INSTANCE)
//            .redefine(CLASS_ITEM)
//            .method(named(NMSMappings.get().methodNameItemGetMaxStackSize)
//                        .and(returns(int.class))
//                        .and(takesNoArguments()))
//            .intercept(Advice.to(ItemInterceptor.class).wrap(StubMethod.INSTANCE))
//            .make()
//            .load(SimpleStack.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());

//        BYTE_BUDDY
//            .with(Implementation.Context.Disabled.Factory.INSTANCE)
//            .redefine(CLASS_ITEM)
//            .visit(Advice.to(ItemInterceptor.class)
//                       .on(named(NMSMappings.get().methodNameItemGetMaxStackSize)
//                               .and(returns(int.class))
//                               .and(takesNoArguments())))
//            .make()
//            .load(SimpleStack.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());

//        BYTE_BUDDY.redefine(CLASS_ITEM_STACK)
//            .method(named(NMSMappings.get().methodNameItemStackGetMaxStackSize))
//            .intercept(Advice.to(ItemStackInterceptor.class))
//            .make()
//            .load(CLASS_ITEM.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
    }

//    private static final class ItemTransformer implements AgentBuilder.Transformer {
//
//        @Override
//        public DynamicType.Builder<?> transform(
//            DynamicType.Builder<?> builder,
//            TypeDescription typeDescription,
//            ClassLoader classLoader,
//            JavaModule module) {
//            return builder.visit(Advice.to(ItemInterceptor.class)
//                       .on(named(NMSMappings.get().methodNameItemGetMaxStackSize)
//                               .and(returns(int.class))
//                               .and(takesNoArguments())));
//        }
//    }

//    public static void test() {
//        System.out.println("HI!");
//    }

    public static class ItemAdvice {
        @Advice.OnMethodEnter
        public static long onMethodEnter() {
            return System.nanoTime();
        }

        @Advice.OnMethodExit
        public static void onMethodExit(@Advice.Return(readOnly = false) int returnValue, @Advice.Enter long startTime, @Advice.This Object item) throws Throwable {
            Plugin plugin = Bukkit.getPluginManager().getPlugin("SimpleStack");
            ClassLoader pluginClassLoader = plugin.getClass().getClassLoader();
            Class<?> interceptClass = Class.forName("com.mikedeejay2.simplestack.bytebuddy.StackSizeTransformer", false, pluginClassLoader);
            Method maxStackSizeMethod = interceptClass.getMethod("getItemMaxStackSize", int.class, long.class, Object.class);
            returnValue = (int) maxStackSizeMethod.invoke(null, returnValue, startTime, item);
        }
    }

    public static int getItemMaxStackSize(int currentReturnValue, long startTime, Object item) {
        String key = item.toString();
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

    private static void printTimings(long startTime) {
        if(!SimpleStack.getInstance().getDebugConfig().isPrintTimings()) return;
        long endTime = System.nanoTime();
        SimpleStack.getInstance().sendInfo(String.format(
            "Item size redirect took %.4f milliseconds to complete",
            ((endTime - startTime) / 1000000.0)));
    }
}
