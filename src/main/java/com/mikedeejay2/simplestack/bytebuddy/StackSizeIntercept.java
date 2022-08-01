package com.mikedeejay2.simplestack.bytebuddy;

import com.mikedeejay2.simplestack.NMSMappings;
import com.mikedeejay2.simplestack.SimpleStack;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.utility.JavaModule;
import org.bukkit.Bukkit;

import static net.bytebuddy.matcher.ElementMatchers.*;

public final class StackSizeIntercept {
    private static final ByteBuddy BYTE_BUDDY = new ByteBuddy();
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
            .transform(new ItemTransformer())
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

    private static final class ItemTransformer implements AgentBuilder.Transformer {

        @Override
        public DynamicType.Builder<?> transform(
            DynamicType.Builder<?> builder,
            TypeDescription typeDescription,
            ClassLoader classLoader,
            JavaModule module) {
            return null;
        }
    }

    public static void test() {
        System.out.println("HI!");
    }

    public static class ItemInterceptor {
        @Advice.OnMethodExit
        public static void getMaxStackSize(@Advice.Return(readOnly = false) int returnValue) throws Throwable {
            returnValue = 64;
            try {
                String test = SimpleStack.class.getName();
            } catch(Exception e) {
                e.printStackTrace();
            }
//            Class<?> clazz = Class.forName(StackSizeIntercept.class.getName(), true, StackSizeIntercept.class.getClassLoader());
//            MethodHandles.Lookup lookup = MethodHandles.lookup();
//            MethodType methodType = MethodType.methodType(void.class);
//            MethodHandle handle = lookup.findStatic(clazz, "test", methodType);
//            handle.invoke();
        }
    }

    private static class ItemStackInterceptor {
        public static int getMaxStackSize() {
            return 64; // TODO: Implement this
        }
    }
}
