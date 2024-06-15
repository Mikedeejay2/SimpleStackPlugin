package com.mikedeejay2.simplestack.bytecode.transformers.asm;

import com.mikedeejay2.simplestack.bytecode.MappedMethodVisitor;
import com.mikedeejay2.simplestack.bytecode.Transformer;
import com.mikedeejay2.simplestack.mappings.MappingEntry;

import static com.mikedeejay2.simplestack.mappings.MappingsLookup.nms;
import static org.objectweb.asm.Opcodes.*;

/**
 * Fixes enchanting tables being unable to enchant any items that are stackable by removing the max stack size check
 * from <code>Item#isEnchantable(ItemStack)</code>
 *
 * @author Mikedeejay2
 */
@Transformer("1.18-1.20.6")
public class TransformItemIsEnchantable extends MappedMethodVisitor {
    protected boolean visitedAload = false;
    protected boolean visitedInvoke = false;

    @Override
    public MappingEntry getMappingEntry() {
        return nms("Item").method("isEnchantable");
    }

    @Override
    public void visitCode() {
        super.visitCode();
//        System.out.println("isEnchantable");
    }

    @Override
    public void visitVarInsn(int opcode, int varIndex) {
        if(!visitedAload && opcode == ALOAD) { // Target first aload (this)
            this.visitedAload = true;
            super.visitVarInsn(ALOAD, 1); // Load ItemStack
            return;
        }
        super.visitVarInsn(opcode, varIndex);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        if(visitedAload && !visitedInvoke && opcode == INVOKEVIRTUAL) { // Target first invoke virtual (getMaxStackSize)
            this.visitedInvoke = true;
            super.visitMethodInsn(INVOKEVIRTUAL, nms("ItemStack").method("getCount"));
            return;
        }
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
    }
}
