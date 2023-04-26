package com.mikedeejay2.simplestack.bytecode.transformers.asm;

import com.mikedeejay2.simplestack.bytecode.MappingsLookup;
import com.mikedeejay2.simplestack.bytecode.MappedMethodVisitor;
import com.mikedeejay2.simplestack.bytecode.Transformer;

import static org.objectweb.asm.Opcodes.*;
import static com.mikedeejay2.simplestack.bytecode.MappingsLookup.*;

/**
 * Used in multiple places, most common is dropping overstacked items onto the ground. This transformation should ensure
 * that all are dropped, not just 1.
 *
 * @author Mikedeejay2
 */
@Transformer({
    "1.19", "1.19.1", "1.19.2", "1.19.3", "1.19.4",
    "1.18", "1.18.1", "1.18.2"
})
public class TransformContainerUtilRemoveItem extends MappedMethodVisitor {
    @Override
    public MappingsLookup.MappingEntry getMappingEntry() {
        return nms("ContainerUtil").method("removeItem");
    }

    @Override
    public void visitCode() {
        super.visitCode();
//        System.out.println("RemoveItem");
        // Uncomment for debug message on visit code
//        debugPrintString("Test of removeItem method");
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        if(equalsMapping(owner, name, descriptor, nms("ItemStack").method("split"))) { // Target invocation of ItemStack#split(I)ItemStack
            redirectSplit();
            return; // Return to not invoke ItemStack#split(I) method
        }

        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
    }

    /**
     * Used in multiple places, most common is dropping overstacked items onto the ground. This method should ensure
     * that all are dropped, not just 1.
     */
    private void redirectSplit() {
        super.visitInsn(POP); // Pop amount
        super.visitInsn(POP); // Pop ItemStack
        // Get the target ItemStack out of the list
        super.visitVarInsn(ALOAD, 0); // Get list
        super.visitVarInsn(ILOAD, 1); // Get slot int
        super.visitMethodInsn(
            INVOKEINTERFACE, "java/util/List", "get",
            "(I)Ljava/lang/Object;", true); // Get ItemStack out of list
        super.visitTypeInsn(CHECKCAST, lastNms().internalName()); // Cast from Object to ItemStack
        super.visitVarInsn(ASTORE, 3); // Store this ItemStack to local index 3

        //// ItemStack itemstack = this.copy();
        super.visitVarInsn(ALOAD, 3); // Load ItemStack
        super.visitMethodInsn(INVOKEVIRTUAL, lastNms().method("copy")); // ItemStack.copy()
        super.visitVarInsn(ASTORE, 4); // Store this new ItemStack to local index 4

        //// itemstack.setCount(j);
        super.visitVarInsn(ALOAD, 4); // Load new ItemStack
        super.visitVarInsn(ILOAD, 2); // Load amount to remove
        super.visitMethodInsn(INVOKEVIRTUAL, lastNms().method("setCount")); // Set the count of the new ItemStack the count of the old

        //// this.shrink(j);
        super.visitVarInsn(ALOAD, 3); // Load ItemStack
        super.visitVarInsn(ILOAD, 2); // Load amount to remove
        super.visitMethodInsn(INVOKEVIRTUAL, lastNms().method("shrink")); // Shrink the old ItemStack by the amount

        //// return itemstack;
        super.visitVarInsn(ALOAD, 4); // Load the new removed ItemStack
        // Next instruction is the return instruction, we don't need to insert an additional one
    }
}
