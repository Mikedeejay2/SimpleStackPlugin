package com.mikedeejay2.simplestack.bytecode.transformers.asm;

import com.mikedeejay2.simplestack.bytecode.MappingsLookup;
import com.mikedeejay2.simplestack.bytecode.MappedMethodVisitor;
import com.mikedeejay2.simplestack.bytecode.Transformer;
import org.objectweb.asm.Label;

import static org.objectweb.asm.Opcodes.*;
import static com.mikedeejay2.simplestack.bytecode.MappingsLookup.*;

/**
 * Fixes stacked damaged items not stacking together upon add. This fixes picking up multiple damaged items from the
 * ground that are duplicates of each other, ensures that they stack properly in the inventory.
 *
 * @author Mikedeejay2
 */
@Transformer({
    "1.20",
    "1.19", "1.19.1", "1.19.2", "1.19.3", "1.19.4",
    "1.18", "1.18.1", "1.18.2"
})
public class TransformPlayerInventoryAdd extends MappedMethodVisitor {
    private boolean visitedIsDamaged = false;
    private boolean visitedIfStatement = false;

    @Override
    public MappingsLookup.MappingEntry getMappingEntry() {
        return nms("PlayerInventory").method("add2");
    }

    @Override
    public void visitCode() {
        super.visitCode();

//        System.out.println("PlayerInventory add");
//        debugPrintString("PlayerInventory add");
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
            super.visitVarInsn(ALOAD, 2); // Load ItemStack
            super.visitMethodInsn(INVOKEVIRTUAL, nms("ItemStack").method("getMaxStackSize")); // Get max stack size
            super.visitInsn(ICONST_1); // Get int of 1
            super.visitJumpInsn(IF_ICMPNE, label); // If damaged item doesn't have a max stack size of 1, treat as a normal item
        }
    }
}
