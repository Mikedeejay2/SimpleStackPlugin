package com.mikedeejay2.simplestack.bytebuddy.transformers.asm;

import com.mikedeejay2.simplestack.bytebuddy.MappingsLookup;
import com.mikedeejay2.simplestack.bytebuddy.MappedMethodVisitor;
import com.mikedeejay2.simplestack.bytebuddy.Transformer;
import org.objectweb.asm.Label;

import static com.mikedeejay2.simplestack.bytebuddy.MappingsLookup.*;
import static org.objectweb.asm.Opcodes.*;

/**
 * Fixes stacked buckets from being replaced by a bucket upon use.
 *
 * @author Mikedeejay2
 */
@Transformer({"1.19"})
public class TransformItemBucketUse extends MappedMethodVisitor {
    protected boolean visitedNew = false;
    protected final Label returnLabel = new Label();
    protected Label existingReturnLabel;
    protected boolean visitedReturnLabel = false;

    @Override
    public MappingsLookup.MappingEntry getMappingEntry() {
        return nms("ItemBucket").method("getEmptySuccessItem");
    }

    @Override
    public void visitCode() {
        super.visitCode();
//        debugPrintString("Test of getEmptySuccessItem method");
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
        if(opcode == NEW && type.equals(nms("ItemStack").internalName())) { // Target new ItemStack creation (new ItemStack(Items.BUCKET);)
            visitedNew = true;
            appendStackedBucketsFix();
        }
        super.visitTypeInsn(opcode, type);
    }

    /**
     * Fixes stacked buckets from being replaced by a bucket upon use.
     */
    private void appendStackedBucketsFix() {
        Label emptyBucketLabel = new Label();
        super.visitVarInsn(ALOAD, 0); // Load ItemStack

        // Shrink ItemStack by one
        super.visitInsn(ICONST_1); // Load int 1
        super.visitMethodInsn(INVOKEVIRTUAL, nms("ItemStack").method("shrink")); // Shrink ItemStack by one
        super.visitVarInsn(ALOAD, 0); // Load ItemStack
        super.visitMethodInsn(INVOKEVIRTUAL, nms("ItemStack").method("isEmpty")); // Get whether ItemStack is empty
        super.visitJumpInsn(IFNE, emptyBucketLabel); // If it is empty, jump to empty bucket
        super.visitFrame(F_SAME, 0, null, 0, null);

        // Get PlayerInventory
        super.visitVarInsn(ALOAD, 1); // Load EntityHuman
        super.visitMethodInsn(INVOKEVIRTUAL, nms("EntityHuman").method("getInventory"));
        // New empty bucket ItemStack
        super.visitTypeInsn(NEW, nms("ItemStack").internalName()); // Create new ItemStack
        super.visitInsn(DUP); // Duplicate this ItemStack on the stack
        super.visitFieldInsn(GETSTATIC, nms("Items").field("BUCKET")); // Get Bucket material
        super.visitMethodInsn(INVOKESPECIAL, nms("ItemStack").method("<init>")); // Call the ItemStack's constructor
        super.visitVarInsn(ASTORE, 2); // Store the new ItemStack to local index 2
        super.visitVarInsn(ALOAD, 2); // Load the new ItemStack
        // Add new bucket to inventory
        super.visitMethodInsn(INVOKEVIRTUAL, nms("PlayerInventory").method("add")); // Attempt to add bucket to inventory
        // If it failed, drop on ground
        Label ifNotDropLabel = new Label();
        super.visitJumpInsn(IFNE, ifNotDropLabel); // If no items need to be dropped, bypass drop method

        super.visitVarInsn(ALOAD, 1); // Load EntityHuman
        super.visitVarInsn(ALOAD, 2); // Load the new ItemStack (empty bucket)
        super.visitInsn(ICONST_0); // Load false (don't throw randomly)
        super.visitInsn(ICONST_1); // Load true (retain ownership of thrown item)
        super.visitMethodInsn(INVOKEVIRTUAL, nms("EntityHuman").method("drop")); // Drop the rest of the item
        super.visitInsn(POP); // Pop the resulting EntityItem

        super.visitLabel(ifNotDropLabel);
        super.visitFrame(F_SAME, 0, null, 0, null);

        // Get ItemStack
        super.visitVarInsn(ALOAD, 0); // Load ItemStack
        // Goto the return label
        super.visitJumpInsn(GOTO, returnLabel); // Goto the return label to return the existing ItemStack

        super.visitLabel(emptyBucketLabel);
        super.visitFrame(F_SAME, 0, null, 0, null);
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        if(visitedNew && !visitedReturnLabel && opcode == GOTO && existingReturnLabel == null) { // Target goto that contains the return label
            existingReturnLabel = label;
        }
        super.visitJumpInsn(opcode, label);
    }

    @Override
    public void visitLabel(Label label) {
        if(label == existingReturnLabel && !visitedReturnLabel) { // Target the return label that was collected above ^
            super.visitLabel(returnLabel); // Add a new label
            visitedReturnLabel = true;
        }
        super.visitLabel(label);
    }
}
