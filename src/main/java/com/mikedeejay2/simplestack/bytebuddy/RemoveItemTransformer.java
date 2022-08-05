package com.mikedeejay2.simplestack.bytebuddy;

import com.mikedeejay2.simplestack.NMSMappings;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.ResettableClassFileTransformer;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.jar.asm.ClassWriter;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;

import java.util.List;

import static net.bytebuddy.matcher.ElementMatchers.*;
import static net.bytebuddy.matcher.ElementMatchers.named;

public class RemoveItemTransformer {
    private static final String CONTAINER_UTIL_CLASS_NAME = NMSMappings.get().classNameContainerUtil;

    private static ResettableClassFileTransformer transformer;

    public static void install() {
        // AgentBuilder for net.minecraft.world.ContainerUtil
        transformer = new AgentBuilder.Default()
            .disableClassFormatChanges()
            .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION) // Use retransformation strategy to modify existing NMS classes
            .type(named(CONTAINER_UTIL_CLASS_NAME)) // Match the ItemStack class
            .transform(((builder, typeDescription, classLoader, module) ->
                builder.visit(new AsmVisitorWrapper.ForDeclaredMethods()
                                  .method(named(NMSMappings.get().methodNameItemStackSplit)
                                              .and(takesArgument(0, List.class))
                                              .and(takesArgument(1, int.class))
                                              .and(takesArgument(2, int.class))
                                              .and(returns(named(NMSMappings.get().classNameItemStack))),
                                          ((it, im, methodVisitor, ic, tp, wf, rf) ->
                                              new RemoveItemVisitor(Opcodes.ASM9, methodVisitor)))
                                  .writerFlags(ClassWriter.COMPUTE_MAXS)))) // Inject SplitMethodVisitor into split() method
            .installOnByteBuddyAgent(); // Inject
    }

    public static void reset() {
        if(transformer != null) {
            transformer.reset(ByteBuddyHolder.getInstrumentation(), AgentBuilder.RedefinitionStrategy.RETRANSFORMATION);
            ByteBuddyHolder.getInstrumentation().removeTransformer(transformer);
        }
    }

    private static final class RemoveItemVisitor extends MethodVisitor {
        private static final String ITEM_STACK_INTERNAL_NAME = NMSMappings.get().classNameItemStack.replace('.', '/');

        private RemoveItemVisitor(int api, MethodVisitor methodVisitor) {
            super(api, methodVisitor);
        }

        @Override
        public void visitCode() {
            super.visitCode();
//            super.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
//            super.visitLdcInsn("Test of removeItem method");
//            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            if(owner.equals(ITEM_STACK_INTERNAL_NAME) && // Target invocation of ItemStack#split(I)ItemStack
                name.equals(NMSMappings.get().methodNameItemStackSplit) &&
                descriptor.startsWith("(I)")) {
                // Get the target ItemStack out of the list
                super.visitVarInsn(Opcodes.ALOAD, 0); // Get list
                super.visitVarInsn(Opcodes.ILOAD, 1); // Get slot int
                super.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/List", "get", "(I)Ljava/lang/Object;", true); // Get ItemStack out of list
                super.visitTypeInsn(Opcodes.CHECKCAST, ITEM_STACK_INTERNAL_NAME); // Cast from Object to ItemStack
                super.visitVarInsn(Opcodes.ASTORE, 3); // Store this ItemStack to local index 3

                // ItemStack itemstack = this.copy();
                super.visitVarInsn(Opcodes.ALOAD, 3); // Load ItemStack
                super.visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    ITEM_STACK_INTERNAL_NAME,
                    NMSMappings.get().methodNameItemStackCopy,
                    String.format("()L%s;", ITEM_STACK_INTERNAL_NAME),
                    false); // ItemStack.copy()
                super.visitVarInsn(Opcodes.ASTORE, 4); // Store this new ItemStack to local index 4

                // itemstack.setCount(j);
                super.visitVarInsn(Opcodes.ALOAD, 4); // Load new ItemStack
                super.visitVarInsn(Opcodes.ILOAD, 2); // Load amount to remove
                super.visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    ITEM_STACK_INTERNAL_NAME,
                    NMSMappings.get().methodNameItemStackSetCount,
                    "(I)V",
                    false); // Set the count of the new ItemStack the count of the old

                // this.shrink(j);
                super.visitVarInsn(Opcodes.ALOAD, 3); // Load ItemStack
                super.visitVarInsn(Opcodes.ILOAD, 2); // Load amount to remove
                super.visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    ITEM_STACK_INTERNAL_NAME,
                    NMSMappings.get().methodNameItemStackShrink,
                    "(I)V",
                    false); // Shrink the old ItemStack by the amount

                // return itemstack;
                super.visitVarInsn(Opcodes.ALOAD, 4); // Load the new removed ItemStack
                // Next instruction is the return instruction, we don't need to insert an additional one
                return; // Return to not invoke ItemStack#split(I) method
            }

            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        }
    }
}
