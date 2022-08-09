package com.mikedeejay2.simplestack.bytebuddy;

import com.mikedeejay2.simplestack.MappingsLookup;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.jar.asm.MethodVisitor;

import static net.bytebuddy.jar.asm.Opcodes.ASM9;

public abstract class SimpleStackMethodVisitor extends MethodVisitor implements SimpleStackWrapper {
    public SimpleStackMethodVisitor() {
        super(ASM9);
    }

    @Override
    public AsmVisitorWrapper.ForDeclaredMethods.MethodVisitorWrapper getWrapper() {
        return (instrumentedType, instrumentedMethod, methodVisitor, implementationContext, typePool, writerFlags, readerFlags) ->
            this.setMethodVisitor(methodVisitor);
    }

    protected final SimpleStackMethodVisitor setMethodVisitor(MethodVisitor visitor) {
        this.mv = visitor;
        return this;
    }

    public void visitMethodInsn(int opcode, MappingsLookup.MappingEntry method, boolean isInterface) {
        super.visitMethodInsn(
            opcode,
            method.owner().internalName(),
            method.name(),
            method.descriptor(),
            isInterface);
    }

    public void visitMethodInsn(int opcode, MappingsLookup.MappingEntry method) {
        visitMethodInsn(opcode, method, false);
    }
}
