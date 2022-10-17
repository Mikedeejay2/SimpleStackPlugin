package com.mikedeejay2.simplestack.bytebuddy.transformers.asm.item;

import com.mikedeejay2.simplestack.bytebuddy.MappedMethodVisitor;
import com.mikedeejay2.simplestack.bytebuddy.Transformer;
import org.objectweb.asm.Label;

import static com.mikedeejay2.simplestack.bytebuddy.MappingsLookup.*;
import static org.objectweb.asm.Opcodes.*;

/**
 * Fixes stacked soups from being replaced by a bowl upon use.
 *
 * @author Mikedeejay2
 */
@Transformer({"1.19", "1.19.1"})
public class TransformItemSoupFinishUsingItem extends MappedMethodVisitor {
    protected boolean visitedNew = false;
    protected boolean visitedReturn = false;
    protected boolean visitedFrame = false;

    @Override
    public MappingEntry getMappingEntry() {
        return nms("ItemSoup").method("finishUsingItem");
    }

    @Override
    public void visitCode() {
        super.visitCode();
//        debugPrintString("Test of finishUsingItem method in ItemSoup");
    }

    @Override
    public void visitInsn(int opcode) {
        if(!visitedReturn && opcode == ARETURN) { // Target the first return statement.
            visitedReturn = true;
        }
        super.visitInsn(opcode);
    }

    @Override
    public void visitFrame(int type, int numLocal, Object[] local, int numStack, Object[] stack) {
        if(!visitedFrame && visitedReturn) { // Target the frame after the first return statement
            visitedFrame = true;
            // Change this frame to include the same locals append the extra ItemStack.
            // Without this, the frame has no local values.
            super.visitFrame(F_APPEND, 1, new Object[] {nms("ItemStack").internalName()}, 0, null);
            return;
        }
        super.visitFrame(type, numLocal, local, numStack, stack);
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
        if(!visitedNew && opcode == NEW && type.equals(nms("ItemStack").internalName())) { // Target new ItemStack() invocation
            visitedNew = true;
            appendStackedSoupFix();
        }
        super.visitTypeInsn(opcode, type);
    }

    /**
     * Fixes stacked soups from being replaced by a bowl upon use.
     */
    private void appendStackedSoupFix() {
        final Label emptyBowlLabel = new Label();

        // Get EntityHuman from EntityLiving
        super.visitVarInsn(ALOAD, 3); // Load EntityLiving
        super.visitTypeInsn(INSTANCEOF, nms("EntityHuman").internalName()); // Check is EntityLiving is EntityHuman
        super.visitJumpInsn(IFEQ, emptyBowlLabel); // If not EntityHuman, goto default behavior

        super.visitVarInsn(ALOAD, 3); // Load EntityLiving
        super.visitTypeInsn(CHECKCAST, nms("EntityHuman").internalName()); // Cast to EntityHuman
        super.visitVarInsn(ASTORE, 5); // Store EntityHuman in local index 5

        // ItemStack is already shrunk by 1, check if it's empty
        super.visitVarInsn(ALOAD, 4); // Load ItemStack
        super.visitMethodInsn(INVOKEVIRTUAL, nms("ItemStack").method("isEmpty")); // Get whether ItemStack is empty

        Label ifNotDropLabel = new Label();
        super.visitJumpInsn(IFNE, ifNotDropLabel); // If it is empty, jump to empty bowl

        // Get PlayerInventory
        super.visitVarInsn(ALOAD, 5); // Load EntityHuman
        super.visitMethodInsn(INVOKEVIRTUAL, nms("EntityHuman").method("getInventory"));
        // New empty bowl ItemStack
        super.visitTypeInsn(NEW, nms("ItemStack").internalName()); // Create new ItemStack
        super.visitInsn(DUP); // Duplicate this ItemStack on the stack
        super.visitFieldInsn(GETSTATIC, nms("Items").field("BOWL")); // Get Bowl material
        super.visitMethodInsn(INVOKESPECIAL, nms("ItemStack").method("<init>")); // Call the ItemStack's constructor
        super.visitVarInsn(ASTORE, 6); // Store the new ItemStack to local index 6
        super.visitVarInsn(ALOAD, 6); // Load the new ItemStack
        // Add new bowl to inventory
        super.visitMethodInsn(INVOKEVIRTUAL, nms("PlayerInventory").method("add")); // Attempt to add bowl to inventory
        // If it failed, drop on ground
        super.visitJumpInsn(IFNE, ifNotDropLabel); // If no items need to be dropped, bypass drop method

        super.visitVarInsn(ALOAD, 5); // Load EntityHuman
        super.visitVarInsn(ALOAD, 6); // Load the new ItemStack (empty bowl)
        super.visitInsn(ICONST_0); // Load false (don't throw randomly)
        super.visitInsn(ICONST_1); // Load true (retain ownership of thrown item)
        super.visitMethodInsn(INVOKEVIRTUAL, nms("EntityHuman").method("drop")); // Drop the rest of the item
        super.visitInsn(POP); // Pop the resulting EntityItem

        super.visitLabel(ifNotDropLabel); // Exit drop if statement
        super.visitFrame(F_SAME, 0, null, 0, null);

        // Get ItemStack
        super.visitVarInsn(ALOAD, 4); // Load ItemStack
        // Goto the return label
        super.visitInsn(ARETURN); // Return the ItemStack
        super.visitLabel(emptyBowlLabel);
        super.visitFrame(F_SAME, 0, null, 0, null);
    }
}
