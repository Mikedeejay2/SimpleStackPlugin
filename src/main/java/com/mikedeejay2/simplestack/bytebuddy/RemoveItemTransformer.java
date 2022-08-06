package com.mikedeejay2.simplestack.bytebuddy;

import com.mikedeejay2.simplestack.SimpleStack;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.ResettableClassFileTransformer;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.jar.asm.ClassWriter;
import net.bytebuddy.jar.asm.MethodVisitor;

import java.util.List;
import java.util.Optional;

import static net.bytebuddy.matcher.ElementMatchers.*;
import static com.mikedeejay2.simplestack.MappingsLookup.*;
import static net.bytebuddy.jar.asm.Opcodes.*;

public class RemoveItemTransformer {
    private static ResettableClassFileTransformer containerUtilTransformer;
    private static ResettableClassFileTransformer slotTransformer;

    public static void install() {
        SimpleStack.getInstance().sendInfo(String.format("Installing \"%s\"...", RemoveItemTransformer.class.getSimpleName()));
        // AgentBuilder for net.minecraft.world.ContainerUtil
        containerUtilTransformer = new AgentBuilder.Default()
            .disableClassFormatChanges()
            .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION) // Use retransformation strategy to modify existing NMS classes
            .type(named(nms("ContainerUtil").qualifiedName())) // Match the ContainerUtil class
            .transform(((builder, typeDescription, classLoader, module) ->
                builder.visit(new AsmVisitorWrapper.ForDeclaredMethods()
                                  .method(named(lastNms().method("removeItem").name())
                                              .and(takesArgument(0, List.class))
                                              .and(takesArgument(1, int.class))
                                              .and(takesArgument(2, int.class))
                                              .and(returns(named(nms("ItemStack").qualifiedName()))),
                                          ((it, im, methodVisitor, ic, tp, wf, rf) ->
                                              new RemoveItemVisitor(ASM9, methodVisitor)))
                                  .writerFlags(ClassWriter.COMPUTE_MAXS)))) // Inject RemoveItemVisitor into removeItem() method
            .installOnByteBuddyAgent(); // Inject

        slotTransformer = new AgentBuilder.Default()
            .disableClassFormatChanges()
            .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION) // Use retransformation strategy to modify existing NMS classes
            .type(named(nms("Slot").qualifiedName())) // Match the Slot class
            .transform(((builder, typeDescription, classLoader, module) ->
                builder.visit(new AsmVisitorWrapper.ForDeclaredMethods()
                                  .method(named(lastNms().method("tryRemove").name())
                                              .and(takesArgument(0, int.class))
                                              .and(takesArgument(1, int.class))
                                              .and(returns(Optional.class)),
                                          ((it, im, methodVisitor, ic, tp, wf, rf) ->
                                              new TryRemoveVisitor(ASM9, methodVisitor)))
                                  .writerFlags(ClassWriter.COMPUTE_MAXS)))) // Inject RemoveItemVisitor into removeItem() method
            .installOnByteBuddyAgent(); // Inject
    }

    public static void reset() {
        ByteBuddyHolder.resetTransformer(containerUtilTransformer);
        ByteBuddyHolder.resetTransformer(slotTransformer);
    }

    private static final class RemoveItemVisitor extends MethodVisitor {
        private RemoveItemVisitor(int api, MethodVisitor methodVisitor) {
            super(api, methodVisitor);
        }

        @Override
        public void visitCode() {
            super.visitCode();
            // Uncomment for debug message on visit code
//            super.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
//            super.visitLdcInsn("Test of removeItem method");
//            super.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            if(owner.equals(nms("ItemStack").internalName()) && // Target invocation of ItemStack#split(I)ItemStack
                name.equals(lastNms().method("split").name()) &&
                descriptor.equals(lastNmsMethod().descriptor())) {
                redirectSplit();
                return; // Return to not invoke ItemStack#split(I) method
            }

            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        }

        private void redirectSplit() {
            // Get the target ItemStack out of the list
            super.visitVarInsn(ALOAD, 0); // Get list
            super.visitVarInsn(ILOAD, 1); // Get slot int
            super.visitMethodInsn(
                INVOKEINTERFACE,
                "java/util/List",
                "get",
                "(I)Ljava/lang/Object;",
                true); // Get ItemStack out of list
            super.visitTypeInsn(CHECKCAST, lastNms().internalName()); // Cast from Object to ItemStack
            super.visitVarInsn(ASTORE, 3); // Store this ItemStack to local index 3

            //// ItemStack itemstack = this.copy();
            super.visitVarInsn(ALOAD, 3); // Load ItemStack
            super.visitMethodInsn(
                INVOKEVIRTUAL,
                lastNms().internalName(),
                lastNms().method("copy").name(),
                lastNmsMethod().descriptor(),
                false); // ItemStack.copy()
            super.visitVarInsn(ASTORE, 4); // Store this new ItemStack to local index 4

            //// itemstack.setCount(j);
            super.visitVarInsn(ALOAD, 4); // Load new ItemStack
            super.visitVarInsn(ILOAD, 2); // Load amount to remove
            super.visitMethodInsn(
                INVOKEVIRTUAL,
                lastNms().internalName(),
                lastNms().method("setCount").name(),
                lastNmsMethod().descriptor(),
                false); // Set the count of the new ItemStack the count of the old

            //// this.shrink(j);
            super.visitVarInsn(ALOAD, 3); // Load ItemStack
            super.visitVarInsn(ILOAD, 2); // Load amount to remove
            super.visitMethodInsn(
                INVOKEVIRTUAL,
                lastNms().internalName(),
                lastNms().method("shrink").name(),
                lastNmsMethod().descriptor(),
                false); // Shrink the old ItemStack by the amount

            //// return itemstack;
            super.visitVarInsn(ALOAD, 4); // Load the new removed ItemStack
            // Next instruction is the return instruction, we don't need to insert an additional one
        }
    }

    private static final class TryRemoveVisitor extends MethodVisitor {
        private TryRemoveVisitor(int api, MethodVisitor methodVisitor) {
            super(api, methodVisitor);
        }

        @Override
        public void visitCode() {
            super.visitCode();
            // Uncomment for debug message on visit code
//            super.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
//            super.visitLdcInsn("Test of tryRemove method");
//            super.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
            if(opcode == INVOKESTATIC &&
                owner.equals("java/lang/Math") &&
                name.equals("min")) { // Target Math.min() invocation
                appendClampToMaxStackSize();
            }
        }

        private void appendClampToMaxStackSize() {
            super.visitVarInsn(ALOAD, 0); // Get this slot
            super.visitMethodInsn(
                INVOKEVIRTUAL,
                nms("Slot").internalName(),
                lastNms().method("getItem").name(),
                lastNmsMethod().descriptor(),
                false); // Get the ItemStack currently in the slot
            super.visitMethodInsn(
                INVOKEVIRTUAL,
                nms("ItemStack").internalName(),
                lastNms().method("getMaxStackSize").name(),
                lastNmsMethod().descriptor(),
                false); // Get the max stack size of the ItemStack in the slot
            super.visitMethodInsn(
                INVOKESTATIC,
                "java/lang/Math",
                "min",
                "(II)I",
                false); // Call Math.min()
            // Next instruction stores the result
        }
    }
}
