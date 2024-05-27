package com.mikedeejay2.simplestack.bytecode.transformers.asm;

import com.mikedeejay2.simplestack.bytecode.MappingsLookup;
import com.mikedeejay2.simplestack.bytecode.MappedMethodVisitor;
import com.mikedeejay2.simplestack.bytecode.Transformer;
import org.objectweb.asm.Label;

import static org.objectweb.asm.Opcodes.*;
import static com.mikedeejay2.simplestack.bytecode.MappingsLookup.*;

/**
 * Fixes shift clicking overstacked items out of a result slot
 * <p>
 * 1.20.6 Fix - All versions of minecraft call <code>istore 6</code> (<code>istore 5</code> on spigot) exactly 4 times.
 * This transformer needs to transform after the 4th time on all versions of Minecraft.
 *
 * @author Mikedeejay2
 */
@Transformer("1.18-1.20.6")
public class TransformContainerMoveItemStackTo extends MappedMethodVisitor {
    private int iStoreCount = 0;
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
    public void visitVarInsn(int opcode, int varIndex) {
        if(opcode == ISTORE && (varIndex == 6 || varIndex == 5)) { // Target flag1
            ++iStoreCount;
        }
        super.visitVarInsn(opcode, varIndex);
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        if(!fixedBreak && iStoreCount == 4 && opcode == GOTO) {
            super.visitVarInsn(ALOAD, 1); // Load ItemStack
            super.visitMethodInsn(INVOKEVIRTUAL, nms("ItemStack").method("isEmpty")); // Get boolean of ItemStack#isEmpty
            super.visitJumpInsn(IFNE, label); // If it is empty, break loop
            fixedBreak = true;
            return; // don't add old break statement
        }
        super.visitJumpInsn(opcode, label);
    }
}
