package com.mikedeejay2.simplestack.bytecode.transformers.asm;

import com.mikedeejay2.simplestack.mappings.MappingEntry;
import com.mikedeejay2.simplestack.bytecode.MappedMethodVisitor;
import com.mikedeejay2.simplestack.bytecode.Transformer;
import org.objectweb.asm.Label;

import static org.objectweb.asm.Opcodes.*;
import static com.mikedeejay2.simplestack.mappings.MappingsLookup.*;

/**
 * Fixes shift clicking overstacked items out of a result slot
 *
 * @author Mikedeejay2
 */
@Transformer("1.18-1.21.10")
public class TransformContainerMoveItemStackTo extends MappedMethodVisitor {
    private boolean visitedSplitInvoke = false;
    private boolean fixedBreak = false;

    @Override
    public MappingEntry getMappingEntry() {
        return nms("Container").method("moveItemStackTo");
    }

    @Override
    public void visitCode() {
        super.visitCode();
//        debugPrintString("Test of moveItemStackTo method");
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        if(!visitedSplitInvoke && opcode == INVOKEVIRTUAL &&
            equalsMapping(owner, name, descriptor, nms("ItemStack").method("split"))) {
            visitedSplitInvoke = true;
        }
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        if(!fixedBreak && visitedSplitInvoke && opcode == GOTO) {
            super.visitVarInsn(ALOAD, 1); // Load ItemStack
            super.visitMethodInsn(INVOKEVIRTUAL, nms("ItemStack").method("isEmpty")); // Get boolean of ItemStack#isEmpty
            super.visitJumpInsn(IFNE, label); // If it is empty, break loop
            fixedBreak = true;
            return; // don't add old break statement
        }
        super.visitJumpInsn(opcode, label);
    }
}
