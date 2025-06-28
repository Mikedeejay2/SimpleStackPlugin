package com.mikedeejay2.simplestack.bytecode.transformers.asm;

import com.mikedeejay2.simplestack.bytecode.MappedMethodVisitor;
import com.mikedeejay2.simplestack.bytecode.Transformer;
import com.mikedeejay2.simplestack.mappings.MappingEntry;
import org.objectweb.asm.Label;

import static com.mikedeejay2.simplestack.mappings.MappingsLookup.nms;
import static org.objectweb.asm.Opcodes.*;

/**
 * Prevents ItemStack#limitSize() from limiting the stack sizes of items to ItemStack#getMaxStackSize().
 * <p>
 * This transform prevents loss of items from overstacked items (Items whose stack size is greater than their max stack
 * size)
 *
 * @author Mikedeejay2
 */
@Transformer("1.20.6-1.21.6")
public class TransformItemStackLimitSize extends MappedMethodVisitor {
    private boolean visitedIcmple = false;

    @Override
    public MappingEntry getMappingEntry() {
        return nms("ItemStack").method("limitSize");
    }

    @Override
    public void visitCode() {
        super.visitCode();
//        System.out.println("LimitSize");
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        super.visitJumpInsn(opcode, label);
        if(!visitedIcmple && opcode == IF_ICMPLE) {
            visitedIcmple = true;
            appendUnlimitStackSize(label);
        }
    }

    /**
     * Adds an if statement that prevents limiting an ItemStack to maxStackSize
     */
    private void appendUnlimitStackSize(Label label) {
        super.visitVarInsn(ALOAD, 0); // Load this ItemStack
        super.visitMethodInsn(INVOKEVIRTUAL, nms("ItemStack").method("getMaxStackSize")); // Invoke ItemStack#getMaxStackSize()
        super.visitVarInsn(ILOAD, 1); // Load maxSize argument
        super.visitJumpInsn(IF_ICMPEQ, label); // If maxStackSize and maxSize are equal, jump to end
    }
}
