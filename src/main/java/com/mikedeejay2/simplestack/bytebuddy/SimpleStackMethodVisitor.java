package com.mikedeejay2.simplestack.bytebuddy;

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
}
