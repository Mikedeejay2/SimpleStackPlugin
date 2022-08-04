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
                                          MoveItemStackToVisitorWrapper.INSTANCE)))) // Inject ItemAdvice into getMaxStackSize() method
            .installOnByteBuddyAgent(); // Inject

    }

    public static void reset() {
        if(transformer != null) {
            transformer.reset(ByteBuddyHolder.getInstrumentation(), AgentBuilder.RedefinitionStrategy.RETRANSFORMATION);
            ByteBuddyHolder.getInstrumentation().removeTransformer(transformer);
        }
    }

    private enum MoveItemStackToVisitorWrapper implements AsmVisitorWrapper.ForDeclaredMethods.MethodVisitorWrapper {
        INSTANCE;

        @Override
        public MethodVisitor wrap(
            TypeDescription instrumentedType,
            MethodDescription instrumentedMethod,
            MethodVisitor methodVisitor,
            Implementation.Context implementationContext,
            TypePool typePool,
            int writerFlags,
            int readerFlags) {
            return new MoveItemStackToMethodVisitor(Opcodes.ASM9, methodVisitor);
        }
    }

//    private static final class ContainerTransformer implements AgentBuilder.Transformer {
//
//        @Override
//        public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module) {
//            return builder.visit(ContainerVisitorWrapper.INSTANCE);
//            return builder.method(
//                named(NMSMappings.get().methodNameContainerMoveItemStackTo)
//                    .and(returns(boolean.class))
//                    .and(takesArgument(1, int.class))
//                    .and(takesArgument(2, int.class))
//                    .and(takesArgument(3, boolean.class)))
//                .intercept(new Implementation.Simple());
//        }
//    }

//    private enum ContainerVisitorWrapper implements AsmVisitorWrapper {
//        INSTANCE;
//
//        @Override
//        public int mergeWriter(int flags) {
//            return flags;
//        }
//
//        @Override
//        public int mergeReader(int flags) {
//            return flags;
//        }
//
//        @Override
//        public ClassVisitor wrap(TypeDescription instrumentedType,
//                                 ClassVisitor classVisitor,
//                                 Implementation.Context implementationContext,
//                                 TypePool typePool,
//                                 FieldList<FieldDescription.InDefinedShape> fields,
//                                 MethodList<?> methods,
//                                 int writerFlags,
//                                 int readerFlags) {
//            return new ContainerClassVisitor(Opcodes.ASM9, classVisitor);
////            return new ClassRemapper(classVisitor, new SimpleRemapper(NMSMappings.get().classNameContainer, ""));
//        }
//    }
//
//    private static final class ContainerClassVisitor extends ClassVisitor {
//        private static final String MOVE_ITEM_STACK_TO_NAME = NMSMappings.get().methodNameContainerMoveItemStackTo;
//        private static final String MOVE_ITEM_STACK_TO_DESCRIPTOR = NMSMappings.get().methodDescriptorContainerMoveItemStackTo;
//
//        private ContainerClassVisitor(int api) {
//            super(api);
//        }
//
//        private ContainerClassVisitor(int api, ClassVisitor classVisitor) {
//            super(api, classVisitor);
//        }
//
//        @Override
//        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
//            if(name.equals(MOVE_ITEM_STACK_TO_NAME) && descriptor.equals(MOVE_ITEM_STACK_TO_DESCRIPTOR)) {
//                System.out.println("oiafdjsf;lkjsd;flkjds;flkjsdf;lkasjdf;lkj");
//                return new MoveItemStackToMethodVisitor(Opcodes.ASM9);
//            }
//            return super.visitMethod(access, name, descriptor, signature, exceptions);
//        }
//    }





    private static final class MoveItemStackToMethodVisitor extends MethodVisitor {
        private boolean lineVisited = false;
        private boolean completed = false;
        private int visitMethodInsnCount = 0;
        private int visitVarInsnCount = 0;

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
                    case 9:
                    {
                        // INVOKEVIRTUAL net/minecraft/world/inventory/Slot.a()I
                        System.out.println("Adding aload stack instruction");
                        super.visitVarInsn(
                            Opcodes.ALOAD,
                            1);

                        System.out.println("Adding max item stack instruction");
                        super.visitMethodInsn(
                            Opcodes.INVOKEVIRTUAL,
                            ITEM_STACK_CLASS_NAME.replace('.', '/'),
                            NMSMappings.get().methodNameItemStackGetMaxStackSize,
                            "()I",
                            false);
                        System.out.println(ITEM_STACK_CLASS_NAME.replace('.', '/') + "." + NMSMappings.get().methodNameItemStackGetMaxStackSize + "()I");

                        System.out.println("Adding math min instruction");
                        super.visitMethodInsn(
                            Opcodes.INVOKESTATIC,
                            "java/lang/Math",
                            "min",
                            "(II)I",
                            false);
                        // INVOKEVIRTUAL net/minecraft/world/item/ItemStack.g(I)V
                    } break;
                }
                completed = visitMethodInsnCount == 10;
                System.out.println(owner + "." + name + descriptor);
            }
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        }

        @Override
        public void visitVarInsn(int opcode, int varIndex) {
            if(lineVisited && !completed) {
                ++visitVarInsnCount;
                switch(visitVarInsnCount) {
                    case 2:
//                    case 5:
//                    case 7:
//                    case 10:
                    {
                        // ALOAD stack
//                        System.out.println("Adding aload stack instruction");
//                        super.visitVarInsn(
//                            Opcodes.ALOAD,
//                            1);
                        // ALOAD slot
                    } break;
                }
                System.out.println("varIndex " + varIndex);
            }
            super.visitVarInsn(opcode, varIndex);
        }

        @Override
        public void visitCode() {
            System.out.println("Visiting code...");
            super.visitCode();
            super.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            super.visitLdcInsn("Test of moveItemStack method");
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);

        }

        @Override
        public void visitMaxs(int maxStack, int maxLocals) {
            System.out.println("MaxStack: " + maxStack + ", MaxLocals: " + maxLocals);
            super.visitMaxs(maxStack, maxLocals);
        }
    }
}
