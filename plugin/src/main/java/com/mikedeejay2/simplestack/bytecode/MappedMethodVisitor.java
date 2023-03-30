package com.mikedeejay2.simplestack.bytecode;

import net.bytebuddy.asm.AsmVisitorWrapper;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public abstract class MappedMethodVisitor extends MethodVisitor implements MethodVisitorInfo {
    public MappedMethodVisitor() {
        super(ASM9);
    }

    @Override
    public AsmVisitorWrapper.ForDeclaredMethods.MethodVisitorWrapper getWrapper() {
        return (instrumentedType, instrumentedMethod, methodVisitor, implementationContext, typePool, writerFlags, readerFlags) ->
            this.setMethodVisitor(methodVisitor);
    }

    protected final MappedMethodVisitor setMethodVisitor(MethodVisitor visitor) {
        this.mv = visitor;
        return this;
    }

    public final void visitMethodInsn(int opcode, MappingsLookup.MappingEntry method, boolean isInterface) {
        super.visitMethodInsn(
            opcode,
            method.owner().internalName(),
            method.name(),
            method.descriptor(),
            isInterface);
    }

    public final void visitMethodInsn(int opcode, MappingsLookup.MappingEntry method) {
        visitMethodInsn(opcode, method, false);
    }

    public final void visitFieldInsn(int opcode, MappingsLookup.MappingEntry field) {
        super.visitFieldInsn(
            opcode,
            field.owner().internalName(),
            field.name(),
            field.descriptor());
    }

    protected final boolean equalsMapping(String owner, String name, String descriptor, MappingsLookup.MappingEntry mapping) {
        return owner.equals(mapping.owner().internalName()) &&
            name.equals(mapping.name()) &&
            descriptor.equals(mapping.descriptor());
    }

    protected final void debugPrintStackTrace() {
        super.visitTypeInsn(NEW, "java/lang/Exception");
        super.visitInsn(DUP);
        super.visitMethodInsn(INVOKESPECIAL, "java/lang/Exception", "<init>", "()V", false);
        super.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Exception", "printStackTrace", "()V", false);
    }

    protected final void debugPrintString(String string) {
        super.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        super.visitLdcInsn(string);
        super.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
    }

    protected final void debugPrintObject(int localIndex) {
        super.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        super.visitVarInsn(ALOAD, localIndex);
        super.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "toString", "()Ljava/lang/String;", false);
        super.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
    }

    protected final void debugPrintInt(int localIndex) {
        super.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        super.visitVarInsn(ILOAD, localIndex);
        super.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(I)Ljava/lang/String;", false);
        super.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "toString", "()Ljava/lang/String;", false);
        super.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
    }
}
