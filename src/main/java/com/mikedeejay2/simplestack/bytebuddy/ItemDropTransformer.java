package com.mikedeejay2.simplestack.bytebuddy;

import com.mikedeejay2.simplestack.NMSMappings;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.ResettableClassFileTransformer;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.jar.asm.ClassWriter;
import net.bytebuddy.jar.asm.Label;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;

import static net.bytebuddy.matcher.ElementMatchers.*;
import static net.bytebuddy.matcher.ElementMatchers.named;

public class ItemDropTransformer {
    private static final String ENTITY_PLAYER_CLASS_NAME = NMSMappings.get().classNameEntityPlayer;

    private static ResettableClassFileTransformer transformer;

    public static void install() {
        // AgentBuilder for net.minecraft.world.item.ItemStack
        transformer = new AgentBuilder.Default()
            .disableClassFormatChanges()
            .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION) // Use retransformation strategy to modify existing NMS classes
            .type(named(ENTITY_PLAYER_CLASS_NAME)) // Match the ItemStack class
            .transform(((builder, typeDescription, classLoader, module) ->
                builder.visit(new AsmVisitorWrapper.ForDeclaredMethods()
                                  .writerFlags(ClassWriter.COMPUTE_MAXS)
                                  .method(named(NMSMappings.get().methodNameEntityPlayerDrop)
                                              .and(takesArgument(0, boolean.class))
                                              .and(returns(boolean.class)),
                                          ((it, im, methodVisitor, ic, tp, wf, rf) ->
                                              new DropMethodVisitor(Opcodes.ASM9, methodVisitor)))))) // Inject SplitMethodVisitor into split() method
            .installOnByteBuddyAgent(); // Inject
    }

    public static void reset() {
        if(transformer != null) {
            transformer.reset(ByteBuddyHolder.getInstrumentation(), AgentBuilder.RedefinitionStrategy.RETRANSFORMATION);
            ByteBuddyHolder.getInstrumentation().removeTransformer(transformer);
        }
    }

    private static final class DropMethodVisitor extends MethodVisitor {
        private int aStoreCount = 0;
        private Label label1;
        private Label label2;
        private Label label3;

        private DropMethodVisitor(int api, MethodVisitor methodVisitor) {
            super(api, methodVisitor);
        }

        @Override
        public void visitCode() {
            super.visitCode();
            super.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            super.visitLdcInsn("Test drop method");
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        }

        @Override
        public void visitVarInsn(int opcode, int varIndex) {
            super.visitVarInsn(opcode, varIndex);
            if(opcode == Opcodes.ASTORE && aStoreCount < 3) {
                ++aStoreCount;
            }

            if(opcode == Opcodes.ASTORE && aStoreCount == 2) {
//
//                label1 = new Label();
//                super.visitLabel(label1);
//                super.visitFrame(
//                    Opcodes.F_APPEND, 2, new Object[]{
//                        NMSMappings.get().classNamePlayerInventory.replace('.', '/'),
//                        NMSMappings.get().classNameItemStack.replace('.', '/')},
//                    0, null);
//                super.visitVarInsn(Opcodes.ALOAD, 3);
//                super.visitMethodInsn(
//                    Opcodes.INVOKEVIRTUAL,
//                    NMSMappings.get().classNameItemStack.replace('.', '/'),
//                    NMSMappings.get().methodNameItemStackGetCount,
//                    "()I",
//                    false);
//                label2 = new Label();
//                super.visitJumpInsn(Opcodes.IFEQ, label2);
//                label3 = new Label();
//                super.visitLabel(label3);
//                // Inside while loop
            }
        }

        @Override
        public void visitJumpInsn(int opcode, Label label) {
            if(opcode == Opcodes.IFNULL) {
//                super.visitJumpInsn(Opcodes.GOTO, label1);
//                super.visitLabel(label2);
//                super.visitFrame(Opcodes.F_SAME, 0, null, 0, null);

//                super.visitInsn(Opcodes.ICONST_1);
//                super.visitInsn(Opcodes.IRETURN);
                return;
            }
            super.visitJumpInsn(opcode, label);
        }

        @Override
        public void visitInsn(int opcode) {
            if(opcode == Opcodes.IRETURN) {



//                System.out.println("ALOAD this");
//                super.visitVarInsn(Opcodes.ALOAD, 0);
//
//                System.out.println("GETFIELD " + NMSMappings.get().classNameEntityHuman.replace('.', '/') + "." + NMSMappings.get().fieldNameEntityHumanContainerMenu + " " + "L" + NMSMappings.get().classNameContainer.replace('.', '/') + ";");
//                super.visitFieldInsn(
//                    Opcodes.GETFIELD,
//                    NMSMappings.get().classNameEntityHuman.replace('.', '/'),
//                    NMSMappings.get().fieldNameEntityHumanContainerMenu,
//                    "L" + NMSMappings.get().classNameContainer.replace('.', '/') + ";");

                ///////////////////// Start old
//                System.out.println("ILOAD entireStack");
//                super.visitVarInsn(Opcodes.ILOAD, 1);

//                System.out.println("ALOAD playerinventory");
//                super.visitVarInsn(Opcodes.ALOAD, 2);
//
//                System.out.println("INVOKEVIRTUAL " + NMSMappings.get().classNamePlayerInventory.replace('.', '/') + "." + NMSMappings.get().methodNamePlayerInventoryGetSelected + "()L" + NMSMappings.get().classNameItemStack.replace('.', '/') + ";");
//                super.visitMethodInsn(
//                    Opcodes.INVOKEVIRTUAL,
//                    NMSMappings.get().classNamePlayerInventory.replace('.', '/'),
//                    NMSMappings.get().methodNamePlayerInventoryGetSelected,
//                    "()L" + NMSMappings.get().classNameItemStack.replace('.', '/') + ";",
//                    false);

//                System.out.println("INVOKEVIRTUAL " + NMSMappings.get().classNameContainer.replace('.', '/') + "." + NMSMappings.get().methodNameContainerSetRemoteSlot + "(IL" + NMSMappings.get().classNameItemStack.replace('.', '/') + ";)V");
//                super.visitMethodInsn(
//                    Opcodes.INVOKEVIRTUAL,
//                    NMSMappings.get().classNameContainer.replace('.', '/'),
//                    NMSMappings.get().methodNameContainerSetRemoteSlot,
//                    "(IL" + NMSMappings.get().classNameItemStack.replace('.', '/') + ";)V",
//                    false);
                //////////////////////////// End old

//                super.visitMethodInsn(
//                    Opcodes.INVOKEVIRTUAL,
//                    NMSMappings.get().classNameContainer.replace('.', '/'),
//                    NMSMappings.get().methodNameContainerSendAllDataToRemote,
//                    "()V",
//                    false);
            }
            super.visitInsn(opcode);
        }
    }
}
