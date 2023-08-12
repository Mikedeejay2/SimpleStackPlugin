package com.mikedeejay2.simplestack.bytecode.transformers.asm;

import com.mikedeejay2.simplestack.bytecode.MappedMethodVisitor;
import com.mikedeejay2.simplestack.bytecode.Transformer;
import org.objectweb.asm.Label;

import static com.mikedeejay2.simplestack.bytecode.MappingsLookup.MappingEntry;
import static com.mikedeejay2.simplestack.bytecode.MappingsLookup.nms;
import static org.objectweb.asm.Opcodes.*;

/**
 * Fixes enchanting tables being unable to enchant any items that are stackable by removing the max stack size check
 * from <code>Item#isEnchantable(ItemStack)</code>
 *
 * @author Mikedeejay2
 */
@Transformer({
    "1.20.1", "1.20",
    "1.19", "1.19.1", "1.19.2", "1.19.3", "1.19.4",
    "1.18", "1.18.1", "1.18.2"
})
public class TransformItemIsEnchantable extends MappedMethodVisitor {
    protected boolean visitedAload = false;
    protected boolean visitedInvoke = false;
    protected boolean visitedIconst = false;
    protected boolean visitedIficmpne = false;

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
        if(!visitedAload && opcode == ALOAD && varIndex == 0) { // Target first aload (this)
            this.visitedAload = true;
            return;
        }
        super.visitVarInsn(opcode, varIndex);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        if(visitedAload && !visitedInvoke && opcode == INVOKEVIRTUAL) { // Target first invoke virtual (getMaxStackSize)
            this.visitedInvoke = true;
            return;
        }
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
    }

    @Override
    public void visitInsn(int opcode) {
        if(visitedInvoke && !visitedIconst && opcode == ICONST_1) { // Target first iconst_1 instruction (1 as max stack)
            this.visitedIconst = true;
            return;
        }
        super.visitInsn(opcode);
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        if(visitedIconst && !visitedIficmpne && opcode == IF_ICMPNE) { // Target first comparison for max stack size
            this.visitedIficmpne = true;
            return;
        }
        super.visitJumpInsn(opcode, label);
    }
}
