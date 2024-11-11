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
@Transformer("1.18-1.21.3")
public class TransformItemStackRestorePatch extends MappedMethodVisitor {
    private boolean visitedInvoke = false;

    @Override
    public MappingEntry getMappingEntry() {
        return nms("ItemStack").method("restorePatch");
    }

    @Override
    public void visitCode() {
        super.visitCode();
//        System.out.println("restorePatch");
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        if(!visitedInvoke && opcode == INVOKEVIRTUAL) {
            visitedInvoke = true;
            appendGetMaxStackSize();
        }
    }

    private void appendGetMaxStackSize() {
        super.visitVarInsn(ALOAD, 0);
        super.visitMethodInsn(INVOKEVIRTUAL, nms("ItemStack").method("getMaxStackSize"));
    }
}
