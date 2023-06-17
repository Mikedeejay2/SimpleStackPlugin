package com.mikedeejay2.simplestack.bytecode.transformers.asm;

import com.mikedeejay2.simplestack.bytecode.MappingsLookup;
import com.mikedeejay2.simplestack.bytecode.MappedMethodVisitor;
import com.mikedeejay2.simplestack.bytecode.Transformer;
import org.objectweb.asm.Label;

import static org.objectweb.asm.Opcodes.*;
import static com.mikedeejay2.simplestack.bytecode.MappingsLookup.*;

/**
 * Fixes ItemStack's isStackable method by removing the isDamaged check. This fixes picking up multiple damaged items
 * from the ground that are duplicates of each other, ensures that they stack properly in the inventory.
 *
 * @author Mikedeejay2
 */
@Transformer({
    "1.20.1", "1.20",
    "1.19", "1.19.1", "1.19.2", "1.19.3", "1.19.4",
    "1.18", "1.18.1", "1.18.2"
})
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
