package com.mikedeejay2.simplestack.bytebuddy;

import com.mikedeejay2.simplestack.MappingsLookup;
import com.mikedeejay2.simplestack.SimpleStack;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.ResettableClassFileTransformer;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.jar.asm.*;

import static net.bytebuddy.matcher.ElementMatchers.*;
import static com.mikedeejay2.simplestack.MappingsLookup.*;
import static net.bytebuddy.jar.asm.Opcodes.*;

public class SplitStackTransformer {
    private static ResettableClassFileTransformer transformer;

    public static void install() {
        SimpleStack.getInstance().sendInfo(String.format("Installing \"%s\"...", SplitStackTransformer.class.getSimpleName()));
        // AgentBuilder for net.minecraft.world.item.ItemStack
        transformer = new AgentBuilder.Default()
            .disableClassFormatChanges()
            .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION) // Use retransformation strategy to modify existing NMS classes
            .type(named(MappingsLookup.nms("ItemStack").qualifiedName())) // Match the ItemStack class
            .transform(((builder, typeDescription, classLoader, module) ->
                builder.visit(new AsmVisitorWrapper.ForDeclaredMethods()
                                  .writerFlags(ClassWriter.COMPUTE_MAXS)
                                  .method(named(MappingsLookup.lastNms().method("split").name())
                                              .and(takesArgument(0, int.class))
                                              .and(returns(named(lastNms().qualifiedName()))),
                                          ((it, im, methodVisitor, ic, tp, wf, rf) ->
                                              new SplitMethodVisitor(ASM9, methodVisitor)))))) // Inject SplitMethodVisitor into split() method
            .installOnByteBuddyAgent(); // Inject
    }

    public static void reset() {
        ByteBuddyHolder.resetTransformer(transformer);
    }

    private static final class SplitMethodVisitor extends MethodVisitor {
        private SplitMethodVisitor(int api, MethodVisitor methodVisitor) {
            super(api, methodVisitor);
        }

        @Override
        public void visitCode() {
            super.visitCode();
            appendClampToMaxStackSize();
        }

        private void appendClampToMaxStackSize() {
            super.visitVarInsn(ALOAD, 0); // Load this ItemStack
            super.visitMethodInsn(
                INVOKEVIRTUAL,
                nms("ItemStack").internalName(),
                lastNms().method("getMaxStackSize").name(),
                lastNmsMethod().descriptor(),
                false); // Invoke ItemStack#getMaxStackSize()
            super.visitVarInsn(ILOAD, 1); // Get split size request
            super.visitMethodInsn(
                INVOKESTATIC,
                "java/lang/Math",
                "min",
                "(II)I",
                false); // Call Math.min() with the max stack size and the split size
            super.visitVarInsn(ISTORE, 1); // Store minimum to split request
        }
    }
}
