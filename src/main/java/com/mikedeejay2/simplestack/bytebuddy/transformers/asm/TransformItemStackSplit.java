package com.mikedeejay2.simplestack.bytebuddy.transformers.asm;

import com.mikedeejay2.simplestack.bytebuddy.MappedMethodVisitor;

import static net.bytebuddy.jar.asm.Opcodes.*;
import static com.mikedeejay2.simplestack.MappingsLookup.*;

public class TransformItemStackSplit extends MappedMethodVisitor {
    @Override
    public MappingEntry getMappingEntry() {
        return nms("ItemStack").method("split");
    }

    @Override
    public void visitCode() {
        super.visitCode();
//        System.out.println("Split");
        appendClampToMaxStackSize();
    }

    private void appendClampToMaxStackSize() {
        super.visitVarInsn(ALOAD, 0); // Load this ItemStack
        super.visitMethodInsn(
            INVOKEVIRTUAL, nms("ItemStack").method("getMaxStackSize")); // Invoke ItemStack#getMaxStackSize()
        super.visitVarInsn(ILOAD, 1); // Get split size request
        super.visitMethodInsn(
            INVOKESTATIC, "java/lang/Math", "min",
            "(II)I", false); // Call Math.min() with the max stack size and the split size
        super.visitVarInsn(ISTORE, 1); // Store minimum to split request
    }
}
