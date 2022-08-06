package com.mikedeejay2.simplestack.bytebuddy;

import com.mikedeejay2.simplestack.MappingsLookup;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.ResettableClassFileTransformer;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.jar.asm.*;

import static net.bytebuddy.matcher.ElementMatchers.*;
import static com.mikedeejay2.simplestack.MappingsLookup.*;

public class StackSplitTransformer {
    private static ResettableClassFileTransformer transformer;

    public static void install() {
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
                                              new SplitMethodVisitor(Opcodes.ASM9, methodVisitor)))))) // Inject SplitMethodVisitor into split() method
            .installOnByteBuddyAgent(); // Inject
    }

    public static void reset() {
        if(transformer != null) {
            transformer.reset(ByteBuddyHolder.getInstrumentation(), AgentBuilder.RedefinitionStrategy.RETRANSFORMATION);
            ByteBuddyHolder.getInstrumentation().removeTransformer(transformer);
        }
    }

    private static final class SplitMethodVisitor extends MethodVisitor {
        private SplitMethodVisitor(int api, MethodVisitor methodVisitor) {
            super(api, methodVisitor);
        }

        @Override
        public void visitCode() {
            super.visitCode();
            this.visitVarInsn(Opcodes.ALOAD, 0); // Load this ItemStack

            super.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                nms("ItemStack").internalName(),
                lastNms().method("getMaxStackSize").name(),
                lastNmsMethod().descriptor(),
                false); // Invoke ItemStack#getMaxStackSize()

            super.visitVarInsn(Opcodes.ILOAD, 1); // Get split size request

            super.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                "java/lang/Math",
                "min",
                "(II)I",
                false); // Call Math.min() with the max stack size and the split size

            super.visitVarInsn(Opcodes.ISTORE, 1); // Store minimum to split request
        }
    }
}
