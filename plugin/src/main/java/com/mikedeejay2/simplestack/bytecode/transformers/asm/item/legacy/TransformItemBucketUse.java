package com.mikedeejay2.simplestack.bytecode.transformers.asm.item.legacy;

import com.mikedeejay2.simplestack.mappings.MappingEntry;
import com.mikedeejay2.simplestack.bytecode.MappedMethodVisitor;
import com.mikedeejay2.simplestack.bytecode.Transformer;

import static com.mikedeejay2.simplestack.mappings.MappingsLookup.*;
import static org.objectweb.asm.Opcodes.*;

/**
 * Fixes stacked buckets from being replaced by a bucket upon use.
 *
 * @author Mikedeejay2
 */
@Transformer("1.18-1.20.6")
public class TransformItemBucketUse extends MappedMethodVisitor {
    protected boolean visitedNew = false;
    protected boolean visitedConstructor = false;

    @Override
    public MappingEntry getMappingEntry() {
        return nms("ItemBucket").method("getEmptySuccessItem");
    }

    @Override
    public String[] getValidationMarkers() {
        return new String[] {"visitedNew", "appendArguments", "visitedConstructor", "appendCreateFilledStack"};
    }

    @Override
    public void visitCode() {
        super.visitCode();
//        debugPrintString("Test of getEmptySuccessItem method");
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
        if(!visitedNew && opcode == NEW && type.equals(nms("ItemStack").internalName())) { // Target new ItemStack creation (new ItemStack(Items.BUCKET);)
            visitedNew = true;
            this.marker("visitedNew");
            appendArguments();
        }
        super.visitTypeInsn(opcode, type);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        if(visitedNew && !visitedConstructor && opcode == INVOKESPECIAL) {
            visitedConstructor = true;
            this.marker("visitedConstructor");
            appendCreateFilledStack();
        }
    }

    private void appendArguments() {
        this.marker("appendArguments");
        super.visitVarInsn(ALOAD, 0); // Load the current ItemStack
        super.visitVarInsn(ALOAD, 1); // Load the player
    }

    private void appendCreateFilledStack() {
        this.marker("appendCreateFilledStack");
        visitInsn(ICONST_1); // Load true boolean for creative override argument
        visitMethodInsn(INVOKESTATIC, nms("ItemUtils").method("createFilledResult")); // Call createFilledResult (proper stack checking)
    }
}
