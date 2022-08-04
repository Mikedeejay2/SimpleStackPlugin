package com.mikedeejay2.simplestack.bytebuddy;

import com.mikedeejay2.simplestack.NMSMappings;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.ResettableClassFileTransformer;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.jar.asm.ClassWriter;
import net.bytebuddy.jar.asm.Label;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.pool.TypePool;
import org.bukkit.Bukkit;

import static net.bytebuddy.matcher.ElementMatchers.*;

public class OverstackFixTransformer {
    private static final Class<?> CLASS_CONTAINER;
    private static final String CLASS_NAME = NMSMappings.get().classNameContainer;
    private static final String ITEM_STACK_CLASS_NAME = NMSMappings.get().classNameItemStack;

    private static ResettableClassFileTransformer transformer;

    static {
        try {
            CLASS_CONTAINER = Class.forName(NMSMappings.get().classNameContainer);
        } catch(ClassNotFoundException e) {
            Bukkit.getLogger().severe("SimpleStack cannot locate NMS classes");
            throw new RuntimeException(e);
        }
    }

    public static void install() {
        // AgentBuilder for net.minecraft.world.inventory.Container
        transformer = new AgentBuilder.Default()
            .disableClassFormatChanges()
//            .with(AgentBuilder.Listener.StreamWriting.toSystemError().withTransformationsOnly())
//            .with(AgentBuilder.InstallationListener.StreamWriting.toSystemError())
            .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION) // Use retransformation strategy to modify existing NMS classes
            .type(is(CLASS_CONTAINER)) // Match the Container class
            .transform(((builder, typeDescription, classLoader, module) ->
                builder.visit(new AsmVisitorWrapper.ForDeclaredMethods()
                                  .writerFlags(ClassWriter.COMPUTE_MAXS)
                                  .method(named(NMSMappings.get().methodNameContainerMoveItemStackTo)
                                              .and(takesArgument(0, named(ITEM_STACK_CLASS_NAME)))
                                              .and(takesArgument(1, int.class))
                                              .and(takesArgument(2, int.class))
                                              .and(takesArgument(3, boolean.class))
                                              .and(returns(boolean.class)),
                                          ((it, im, methodVisitor, ic, tp, wf, rf) ->
                                              new MoveItemStackToMethodVisitor(Opcodes.ASM9, methodVisitor)))))) // Inject ItemAdvice into getMaxStackSize() method
            .installOnByteBuddyAgent(); // Inject

    }

    public static void reset() {
        if(transformer != null) {
            transformer.reset(ByteBuddyHolder.getInstrumentation(), AgentBuilder.RedefinitionStrategy.RETRANSFORMATION);
            ByteBuddyHolder.getInstrumentation().removeTransformer(transformer);
        }
    }

    private static final class MoveItemStackToMethodVisitor extends MethodVisitor {
        private boolean lineVisited = false;
        private boolean completed = false;
        private int visitMethodInsnCount = 0;

        private MoveItemStackToMethodVisitor(int api, MethodVisitor methodVisitor) {
            super(api, methodVisitor);
        }

        @Override
        public void visitLineNumber(int line, Label start) {
            if(!completed && line == NMSMappings.get().methodLineNumberContainerMoveItemStackTo) {
                this.lineVisited = true;
            }
            super.visitLineNumber(line, start);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            if(lineVisited && !completed) {
                ++visitMethodInsnCount;
                switch(visitMethodInsnCount) {
                    case 2:
                    case 4:
                    case 7:
                    case 9: {
                        super.visitVarInsn(
                            Opcodes.ALOAD,
                            1);

                        super.visitMethodInsn(
                            Opcodes.INVOKEVIRTUAL,
                            ITEM_STACK_CLASS_NAME.replace('.', '/'),
                            NMSMappings.get().methodNameItemStackGetMaxStackSize,
                            "()I",
                            false);

                        super.visitMethodInsn(
                            Opcodes.INVOKESTATIC,
                            "java/lang/Math",
                            "min",
                            "(II)I",
                            false);
                    } break;
                }
                completed = visitMethodInsnCount == 9;
            }
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        }

//        @Override
//        public void visitCode() {
//            System.out.println("Visiting code...");
//            super.visitCode();
//            super.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
//            super.visitLdcInsn("Test of moveItemStackTo method");
//            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
//        }
    }
}
