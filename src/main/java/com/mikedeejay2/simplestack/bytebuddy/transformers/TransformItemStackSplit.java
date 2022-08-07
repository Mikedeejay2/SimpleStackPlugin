package com.mikedeejay2.simplestack.bytebuddy.transformers;

import com.mikedeejay2.simplestack.bytebuddy.SimpleStackMethodVisitor;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;

import static net.bytebuddy.jar.asm.Opcodes.*;
import static net.bytebuddy.matcher.ElementMatchers.*;
import static com.mikedeejay2.simplestack.MappingsLookup.*;

public final class TransformItemStackSplit extends SimpleStackMethodVisitor {
    @Override
    public ElementMatcher.Junction<? super MethodDescription> getMatcher() {
        return named(getMappingEntry().name())
            .and(takesArgument(0, int.class))
            .and(returns(named(lastNms().qualifiedName())));
    }

    @Override
    public MappingEntry getMappingEntry() {
        return nms("ItemStack").method("split");
    }

    @Override
    public void visitCode() {
        super.visitCode();
        appendClampToMaxStackSize();
    }

    private void appendClampToMaxStackSize() {
        super.visitVarInsn(ALOAD, 0); // Load this ItemStack
        super.visitMethodInsn(
            INVOKEVIRTUAL,
            nms("ItemStack").internalName(),
            lastNms().method("getMaxStackSize").name(),
            lastNmsMethod().descriptor(),
            false); // Invoke ItemStack#getMaxStackSize()
        super.visitVarInsn(ILOAD, 1); // Get split size request
        super.visitMethodInsn(
            INVOKESTATIC,
            "java/lang/Math",
            "min",
            "(II)I",
            false); // Call Math.min() with the max stack size and the split size
        super.visitVarInsn(ISTORE, 1); // Store minimum to split request
    }
}
