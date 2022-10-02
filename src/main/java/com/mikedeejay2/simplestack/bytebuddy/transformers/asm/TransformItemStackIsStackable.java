package com.mikedeejay2.simplestack.bytebuddy.transformers.asm;

import com.mikedeejay2.simplestack.MappingsLookup;
import com.mikedeejay2.simplestack.bytebuddy.MappedMethodVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import static org.objectweb.asm.Opcodes.*;
import static com.mikedeejay2.simplestack.MappingsLookup.*;

/**
 * Fixes ItemStack's isStackable method by removing the isDamaged check. This fixes picking up multiple damaged items
 * from the ground that are duplicates of each other, ensures that they stack properly in the inventory.
 *
 * @author Mikedeejay2
 */
public class TransformItemStackIsStackable extends MappedMethodVisitor {
    private boolean visitedIfStatement = false;
    private boolean visitedIConst = false;

    @Override
    public MappingsLookup.MappingEntry getMappingEntry() {
        return nms("ItemStack").method("isStackable");
    }

    @Override
    public void visitCode() {
        super.visitCode();
//        System.out.println("isStackable");
//        debugPrintString("IsStackable");
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        if(visitedIfStatement && !visitedIConst) {
            return;
        }
        super.visitJumpInsn(opcode, label);
        if(opcode == IF_ICMPLE && !visitedIfStatement) {
            visitedIfStatement = true;
        }
    }

    @Override
    public void visitInsn(int opcode) {
        if(opcode == ICONST_1 && visitedIfStatement) {
            visitedIConst = true;
        }
        super.visitInsn(opcode);
    }

    @Override
    public void visitVarInsn(int opcode, int varIndex) {
        if(visitedIfStatement && !visitedIConst) {
            return;
        }
        super.visitVarInsn(opcode, varIndex);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        if(visitedIfStatement && !visitedIConst) {
            return;
        }
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
    }
}
