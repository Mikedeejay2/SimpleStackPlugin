package com.mikedeejay2.simplestack.bytebuddy.transformers.asm;

import com.mikedeejay2.simplestack.bytebuddy.MappingsLookup;
import com.mikedeejay2.simplestack.bytebuddy.MappedMethodVisitor;
import com.mikedeejay2.simplestack.bytebuddy.Transformer;
import org.objectweb.asm.Label;

import static org.objectweb.asm.Opcodes.*;
import static com.mikedeejay2.simplestack.bytebuddy.MappingsLookup.*;

/**
 * Fixes shift clicking overstacked items out of a result slot
 *
 * @author Mikedeejay2
 */
@Transformer({
    "1.19", "1.19.1", "1.19.2", "1.19.3",
    "1.18", "1.18.1"
})
public class TransformContainerMoveItemStackTo extends MappedMethodVisitor {
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
