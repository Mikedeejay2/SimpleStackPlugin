package com.mikedeejay2.simplestack.bytebuddy;

import com.mikedeejay2.simplestack.NMSMappings;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.ResettableClassFileTransformer;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.jar.asm.*;
import org.bukkit.Bukkit;

import static net.bytebuddy.matcher.ElementMatchers.*;

public class StackSplitTransformer {
    private static final String ITEM_STACK_CLASS_NAME = NMSMappings.get().classNameItemStack;

    private static ResettableClassFileTransformer transformer;

    public static void install() {
        // AgentBuilder for net.minecraft.world.item.ItemStack
        transformer = new AgentBuilder.Default()
            .disableClassFormatChanges()
            .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION) // Use retransformation strategy to modify existing NMS classes
            .type(named(ITEM_STACK_CLASS_NAME)) // Match the ItemStack class
            .transform(((builder, typeDescription, classLoader, module) ->
                builder.visit(new AsmVisitorWrapper.ForDeclaredMethods()
                                  .writerFlags(ClassWriter.COMPUTE_MAXS)
                                  .method(named(NMSMappings.get().methodNameItemStackSplit)
                                              .and(takesArgument(0, int.class))
                                              .and(returns(named(ITEM_STACK_CLASS_NAME))),
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
//            super.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
//            super.visitLdcInsn("Test of split method");
//            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
//
//            super.visitVarInsn(Opcodes.ILOAD, 1);
//            super.visitVarInsn(Opcodes.ALOAD, 0);
//            super.visitFieldInsn(
//                Opcodes.GETFIELD,
//                ITEM_STACK_CLASS_NAME.replace('.', '/'),
//                NMSMappings.get().fieldNameItemStackCount,
//                "I");
//            Label falseLabel = new Label();
//            super.visitJumpInsn(Opcodes.IF_ICMPNE, falseLabel);
//            Label trueLabel = new Label();
//            super.visitLabel(trueLabel);

            this.visitVarInsn(Opcodes.ALOAD, 0);

            super.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                ITEM_STACK_CLASS_NAME.replace('.', '/'),
                NMSMappings.get().methodNameItemStackGetMaxStackSize,
                "()I",
                false);

            super.visitVarInsn(Opcodes.ILOAD, 1);

            super.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                "java/lang/Math",
                "min",
                "(II)I",
                false);

            super.visitVarInsn(Opcodes.ISTORE, 1);

//
//            super.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
//            super.visitVarInsn(Opcodes.ILOAD, 1);
//            super.visitInvokeDynamicInsn("makeConcatWithConstants", "(I)Ljava/lang/String;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/StringConcatFactory", "makeConcatWithConstants", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite;", false), new Object[]{"True block \u0001"});
//            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
//
//            super.visitLabel(falseLabel);
//            super.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        }
    }
}
