package com.mikedeejay2.simplestack.bytecode.transformers.asm;

import com.mikedeejay2.simplestack.bytecode.MappedMethodVisitor;
import com.mikedeejay2.simplestack.bytecode.Transformer;
import com.mikedeejay2.simplestack.mappings.MappingEntry;

import static com.mikedeejay2.simplestack.mappings.MappingsLookup.nms;
import static org.objectweb.asm.Opcodes.*;

/**
 * Used to prevent overstacked creative spawned items from ghosting serverside. If the spawned ItemStack is greater than
 * the max stack size, set the count to the actual max stack size.
 *
 * @author Mikedeejay2
 */
@Transformer("1.20.6")
public class TransformHandleCreativeModeSlot extends MappedMethodVisitor {
    private final int itemStackIdx = 3;
    private boolean visitedItemStack = false;
    private boolean visitedAstore = false;


    @Override
    public MappingEntry getMappingEntry() {
        return nms("PlayerConnection").method("handleSetCreativeModeSlot");
    }

    @Override
    public void visitCode() {
        super.visitCode();
//        System.out.println("handleSetCreativeModeSlot");
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        if(!visitedItemStack && opcode == INVOKEVIRTUAL && equalsMapping( // Target itemStack variable
            owner, name, descriptor, nms("PacketPlayInSetCreativeSlot").method("itemStack"))) {
            visitedItemStack = true;
        }
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
    }

    @Override
    public void visitVarInsn(int opcode, int varIndex) {
        super.visitVarInsn(opcode, varIndex);
        if(visitedItemStack && !visitedAstore && opcode == ASTORE && varIndex == itemStackIdx) { // Target astore itemStack
            visitedAstore = true;
            appendItemStackLimit();
        }
    }

    /**
     * Adds the following code
     * <pre>
     * itemStack = itemStack.setCount(Math.min(itemStack.getMaxStackSize(), itemStack.getCount()));
     * </pre>
     */
    private void appendItemStackLimit() {
        super.visitVarInsn(ALOAD, itemStackIdx); // Load ItemStack (for later)
        super.visitInsn(DUP); // Load ItemStack again
        super.visitMethodInsn(INVOKEVIRTUAL, nms("ItemStack").method("getMaxStackSize")); // Get the max stack size of the ItemStack
        super.visitVarInsn(ALOAD, itemStackIdx); // Load ItemStack
        super.visitMethodInsn(INVOKEVIRTUAL, nms("ItemStack").method("getCount")); // Get the current count of the ItemStack
        super.visitMethodInsn(
            INVOKESTATIC, "java/lang/Math", "min",
            "(II)I", false); // Call Math.min() with the min stack size and the current size
        super.visitMethodInsn(INVOKEVIRTUAL, nms("ItemStack").method("setCount")); // Set the item to the minimum amount
    }
}
