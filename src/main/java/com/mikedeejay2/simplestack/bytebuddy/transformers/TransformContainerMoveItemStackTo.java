package com.mikedeejay2.simplestack.bytebuddy.transformers;

import com.mikedeejay2.simplestack.MappingsLookup;
import com.mikedeejay2.simplestack.bytebuddy.SimpleStackMethodVisitor;
import net.bytebuddy.jar.asm.Label;

import static net.bytebuddy.jar.asm.Opcodes.*;
import static com.mikedeejay2.simplestack.MappingsLookup.*;

public class TransformContainerMoveItemStackTo extends SimpleStackMethodVisitor {
    private boolean visitedGetMaxStackSize = false;
    private boolean visitedIStoreFlag = false;
    private boolean fixedBreak = false;

    @Override
    public MappingsLookup.MappingEntry getMappingEntry() {
        return nms("Container").method("moveItemStackTo");
    }

    @Override
    public void visitCode() {
        super.visitCode();
//        debugPrintString("Test of moveItemStackTo method");
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        if(!visitedGetMaxStackSize && opcode == INVOKEVIRTUAL &&
            equalsMapping(owner, name, descriptor, nms("Slot").method("getMaxStackSize"))) {
            visitedGetMaxStackSize = true;
        }
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
    }

    @Override
    public void visitVarInsn(int opcode, int varIndex) {
        // Targets the last statement before the break line
        if(!visitedIStoreFlag && visitedGetMaxStackSize && opcode == ISTORE && varIndex == 6) { // Target flag1
            visitedIStoreFlag = true;
        }
        super.visitVarInsn(opcode, varIndex);
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        if(!fixedBreak && visitedIStoreFlag && opcode == GOTO) {
            super.visitVarInsn(ALOAD, 1); // Load ItemStack
            super.visitMethodInsn(INVOKEVIRTUAL, nms("ItemStack").method("isEmpty")); // Get boolean of ItemStack#isEmpty
            super.visitJumpInsn(IFNE, label); // If it is empty, break loop
            fixedBreak = true;
            return; // don't add old break statement
        }
        super.visitJumpInsn(opcode, label);
    }
}
