package com.mikedeejay2.simplestack.bytecode.transformers.asm;

import com.mikedeejay2.simplestack.bytecode.MappedMethodVisitor;
import com.mikedeejay2.simplestack.bytecode.Transformer;
import com.mikedeejay2.simplestack.mappings.MappingEntry;

import static com.mikedeejay2.simplestack.mappings.MappingsLookup.nms;
import static org.objectweb.asm.Opcodes.*;

/**
 * Calls <code>ItemStack#getMaxStackSize</code> at the end of ItemStack constructors. This allows new ItemStacks to
 * stack with existing ItemStacks, as calling <code>getMaxStackSize</code> applies the appropriate components to the
 * stack.
 *
 * @author Mikedeejay2
 */
@Transformer("1.20.6-1.21.11")
public class TransformItemStackInit extends MappedMethodVisitor {
    private boolean visitedReturn = false;

    @Override
    public MappingEntry getMappingEntry() {
        return nms("ItemStack").method("<init2>");
    }

    @Override
    public String[] getValidationMarkers() {
        return new String[] {"visitedReturn", "appendGetMaxStackSize"};
    }

    @Override
    public void visitCode() {
        super.visitCode();
//        System.out.println("initfull");
    }

    @Override
    public void visitInsn(int opcode) {
        if(!visitedReturn && opcode == RETURN) {
            visitedReturn = true;
            this.marker("visitedReturn");
            appendGetMaxStackSize();
        }
        super.visitInsn(opcode);
    }

    private void appendGetMaxStackSize() {
        this.marker("appendGetMaxStackSize");
        super.visitVarInsn(ALOAD, 0);
        super.visitMethodInsn(INVOKEVIRTUAL, nms("ItemStack").method("getMaxStackSize"));
    }
}
