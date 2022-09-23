package com.mikedeejay2.simplestack.bytebuddy.transformers.asm;

import com.mikedeejay2.simplestack.MappingsLookup;
import com.mikedeejay2.simplestack.bytebuddy.MappedMethodVisitor;
import net.bytebuddy.jar.asm.Label;

import static net.bytebuddy.jar.asm.Opcodes.*;
import static com.mikedeejay2.simplestack.MappingsLookup.*;

/**
 * Fixes stacked damaged items not stacking together upon add
 *
 * @author Mikedeejay2
 */
public class TransformPlayerInventoryAdd extends MappedMethodVisitor {
    private boolean visitedIsDamaged = false;
    private boolean visitedIfStatement = false;

    @Override
    public MappingsLookup.MappingEntry getMappingEntry() {
        return nms("PlayerInventory").method("add");
    }

    @Override
    public void visitCode() {
        super.visitCode();

        System.out.println("PlayerInventory add");
        debugPrintString("PlayerInventory add");
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        if(opcode == INVOKEVIRTUAL &&
            equalsMapping(owner, name, descriptor, nms("ItemStack").method("isDamaged"))) {
            visitedIsDamaged = true;
        }
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        super.visitJumpInsn(opcode, label);
        if(visitedIsDamaged && !visitedIfStatement) {
            visitedIfStatement = true;
            super.visitJumpInsn(GOTO, label);
        }
    }
}
