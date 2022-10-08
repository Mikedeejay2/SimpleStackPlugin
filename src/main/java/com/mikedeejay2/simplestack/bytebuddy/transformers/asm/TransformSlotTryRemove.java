package com.mikedeejay2.simplestack.bytebuddy.transformers.asm;

import com.mikedeejay2.simplestack.bytebuddy.MappingsLookup;
import com.mikedeejay2.simplestack.bytebuddy.MappedMethodVisitor;
import com.mikedeejay2.simplestack.bytebuddy.Transformer;

import static org.objectweb.asm.Opcodes.*;
import static com.mikedeejay2.simplestack.bytebuddy.MappingsLookup.*;

/**
 * Make sure max of max stack size is removed from trying to remove from a slot.
 *
 * @author Mikedeejay2
 */
@Transformer({"1.19"})
public class TransformSlotTryRemove extends MappedMethodVisitor {
    @Override
    public MappingsLookup.MappingEntry getMappingEntry() {
        return nms("Slot").method("tryRemove");
    }

    @Override
    public void visitCode() {
        super.visitCode();
//        System.out.println("TryRemove");
        // Uncomment for debug message on visit code
//        debugPrintString("Test of tryRemove method");
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        if(opcode == INVOKESTATIC && owner.equals("java/lang/Math") && name.equals("min")) { // Target Math.min() invocation
            appendClampToMaxStackSize();
        }
    }

    /**
     * Make sure max of max stack size is removed from trying to remove from a slot.
     */
    private void appendClampToMaxStackSize() {
        super.visitVarInsn(ALOAD, 0); // Get this slot
        super.visitMethodInsn(INVOKEVIRTUAL, nms("Slot").method("getItem")); // Get the ItemStack currently in the slot
        super.visitMethodInsn(INVOKEVIRTUAL, nms("ItemStack").method("getMaxStackSize")); // Get the max stack size of the ItemStack in the slot
        super.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "min", "(II)I", false); // Call Math.min()
        // Next instruction stores the result
    }
}
