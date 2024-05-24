package com.mikedeejay2.simplestack.bytecode.transformers.asm.item;

import com.mikedeejay2.simplestack.bytecode.MappedMethodVisitor;
import com.mikedeejay2.simplestack.bytecode.Transformer;
import org.objectweb.asm.Label;

import static com.mikedeejay2.simplestack.bytecode.MappingsLookup.MappingEntry;
import static com.mikedeejay2.simplestack.bytecode.MappingsLookup.nms;
import static org.objectweb.asm.Opcodes.*;

/**
 * Fixes stacked solid buckets from being replaced by a bucket upon use.
 *
 * @author Mikedeejay2
 */
@Transformer("1.20.6")
public class TransformItemSolidBucketUse extends MappedMethodVisitor {
    protected boolean aStore = false;
    protected final Label afterLabel = new Label();
    protected boolean visitedGetStatic = false;
    protected int invokeVirtualCount = 0;
    protected boolean visitedFrame = false;

    @Override
    public MappingEntry getMappingEntry() {
        return nms("SolidBucketItem").method("useOn");
    }

    @Override
    public void visitCode() {
        super.visitCode();
//        debugPrintString("Test of getEmptySuccessItem method");
    }

    @Override
    public void visitVarInsn(int opcode, int varIndex) {
        super.visitVarInsn(opcode, varIndex);
        if(!aStore && opcode == ASTORE && varIndex == 4) {
            aStore = true;
            appendStackedBucketsFix();
        }
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        if(aStore && !visitedGetStatic && opcode == GETSTATIC && equalsMapping(
            owner, name, descriptor, nms("Items").field("BUCKET"))) { // Get Items.BUCKET instruction
            visitedGetStatic = true;
        }
        super.visitFieldInsn(opcode, owner, name, descriptor);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        // Add afterLabel after player's hand is set to a bucket
        if(visitedGetStatic && invokeVirtualCount < 2 && opcode == INVOKEVIRTUAL) { // Target setItemInHand and getDefaultInstance()
            ++invokeVirtualCount;
            if(invokeVirtualCount != 2) return;
            super.visitLabel(afterLabel);
        }
    }

    /**
     * Fixes stacked buckets from being replaced by a bucket upon use.
     */
    private void appendStackedBucketsFix() {
        Label emptyBucketLabel = new Label();
        super.visitVarInsn(ALOAD, 1); // Load useOnContext argument
        super.visitMethodInsn(INVOKEVIRTUAL, nms("UseOnContext").method("getItemInHand")); // Get the ItemStack used
        super.visitVarInsn(ASTORE, 5); // Save to index 5 (first unused index)


        // Shrink ItemStack by one
//        super.visitVarInsn(ALOAD, 5); // Load ItemStack
//        super.visitInsn(ICONST_1); // Load int 1
//        super.visitMethodInsn(INVOKEVIRTUAL, nms("ItemStack").method("shrink")); // Shrink ItemStack by one

        super.visitVarInsn(ALOAD, 5); // Load ItemStack
        super.visitMethodInsn(INVOKEVIRTUAL, nms("ItemStack").method("isEmpty")); // Get whether ItemStack is empty
        super.visitJumpInsn(IFNE, emptyBucketLabel); // If it is empty, jump to empty bucket

        // Get PlayerInventory
        super.visitVarInsn(ALOAD, 3); // Load EntityHuman (Player)
        super.visitMethodInsn(INVOKEVIRTUAL, nms("EntityHuman").method("getInventory"));
        // New empty bucket ItemStack
        super.visitTypeInsn(NEW, nms("ItemStack").internalName()); // Create new ItemStack
        super.visitInsn(DUP); // Duplicate this ItemStack on the stack
        super.visitFieldInsn(GETSTATIC, nms("Items").field("BUCKET")); // Get Bucket material
        super.visitMethodInsn(INVOKESPECIAL, nms("ItemStack").method("<init>")); // Call the ItemStack's constructor
        super.visitVarInsn(ASTORE, 6); // Store the new ItemStack to local index 6
        super.visitVarInsn(ALOAD, 6); // Load the new ItemStack
        // Add new bucket to inventory
        super.visitMethodInsn(INVOKEVIRTUAL, nms("PlayerInventory").method("add")); // Attempt to add bucket to inventory
        // If it failed, drop on ground
        Label ifNotDropLabel = new Label();
        super.visitJumpInsn(IFNE, ifNotDropLabel); // If no items need to be dropped, bypass drop method

        super.visitVarInsn(ALOAD, 3); // Load EntityHuman
        super.visitVarInsn(ALOAD, 6); // Load the new ItemStack (empty bucket)
        super.visitInsn(ICONST_0); // Load false (don't throw randomly)
        super.visitInsn(ICONST_1); // Load true (retain ownership of thrown item)
        super.visitMethodInsn(INVOKEVIRTUAL, nms("EntityHuman").method("drop")); // Drop the rest of the item
        super.visitInsn(POP); // Pop the resulting EntityItem

        super.visitLabel(ifNotDropLabel);

        // Goto the return label
        super.visitJumpInsn(GOTO, afterLabel); // Goto the return label to return the existing ItemStack

        super.visitLabel(emptyBucketLabel);
    }
}
