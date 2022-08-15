package com.mikedeejay2.simplestack.bytebuddy.transformers;

import com.mikedeejay2.simplestack.MappingsLookup;
import com.mikedeejay2.simplestack.bytebuddy.SimpleStackMethodVisitor;

import static net.bytebuddy.jar.asm.Opcodes.*;
import static com.mikedeejay2.simplestack.MappingsLookup.*;

public final class TransformContainerUtilRemoveItem extends SimpleStackMethodVisitor {
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
        super.visitMethodInsn(
            INVOKEVIRTUAL, lastNms().method("shrink")); // Shrink the old ItemStack by the amount

        //// return itemstack;
        super.visitVarInsn(ALOAD, 4); // Load the new removed ItemStack
        // Next instruction is the return instruction, we don't need to insert an additional one
    }
}