package com.mikedeejay2.simplestack.bytecode.transformers.asm.item;

import com.mikedeejay2.simplestack.bytecode.MappedMethodVisitor;
import com.mikedeejay2.simplestack.bytecode.Transformer;
import com.mikedeejay2.simplestack.mappings.MappingEntry;
import org.objectweb.asm.Label;

import static com.mikedeejay2.simplestack.mappings.MappingsLookup.nms;
import static org.objectweb.asm.Opcodes.*;

/**
 * Fixes stacked solid buckets from being replaced by a bucket upon use.
 *
 * @author Mikedeejay2
 */
@Transformer("1.20-1.20.6")
public class TransformItemSolidBucketUse extends MappedMethodVisitor {
    protected boolean visitedAStore = false;
    protected boolean visitedGetStatic = false;
    protected boolean visitedInvoke = false;
    protected boolean visitedSetItemInHand = false;

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
        if(!visitedAStore && opcode == ASTORE && varIndex == 4) {
            visitedAStore = true;
            appendStackedBucketsFix();
        }
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        if(visitedAStore && !visitedGetStatic && opcode == GETSTATIC && equalsMapping(
            owner, name, descriptor, nms("Items").field("BUCKET"))) { // Get Items.BUCKET instruction
            visitedGetStatic = true;
            // Load stack to be used in setItemInHand call
            super.visitVarInsn(ALOAD, 5);
            return;
        }
        super.visitFieldInsn(opcode, owner, name, descriptor);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        if(visitedGetStatic && !visitedInvoke && opcode == INVOKEVIRTUAL) { // Target setItemInHand and getDefaultInstance()
            visitedInvoke = true;
            // Cancel invocation of getDefaultInstance on Items.BUCKET
            return;
        }
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        if(visitedInvoke && !visitedSetItemInHand) {
            visitedSetItemInHand = true;
            appendInventoryUpdate();
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

        super.visitLabel(emptyBucketLabel);

        // If the existing stack is empty, set it to a bucket
        super.visitTypeInsn(NEW, nms("ItemStack").internalName()); // Create new ItemStack
        super.visitInsn(DUP); // Duplicate this ItemStack on the stack
        super.visitFieldInsn(GETSTATIC, nms("Items").field("BUCKET")); // Get Bucket material
        super.visitMethodInsn(INVOKESPECIAL, nms("ItemStack").method("<init>")); // Call the ItemStack's constructor
        super.visitVarInsn(ASTORE, 5); // Store the existing stack variable

        super.visitLabel(ifNotDropLabel);
    }

    /**
     * Prevents the powdered snow bucket from "jumping" in the inventory upon use. Manually send the new data of the
     * inventory to the client to prevent the effect from occurring.
     * <p>
     * ItemBucket already does this as it is called using the <code>use</code> method, however the ItemSolidBucket class
     * uses the <code>useOn</code> method, which does not do this by default.
     */
    private void appendInventoryUpdate() {
        final Label afterLabel = new Label();
        // if(!entityPlayer.isUsingItem()) {
        super.visitVarInsn(ALOAD, 3); // Load entityPLayer
        super.visitMethodInsn(INVOKEVIRTUAL, nms("EntityLiving").method("isUsingItem"));
        super.visitJumpInsn(IFNE, afterLabel); // If using item, skip if body

        // entityPlayer.inventoryMenu.sendAllDataToRemote();
        super.visitVarInsn(ALOAD, 3); // Load entityPLayer
        super.visitFieldInsn(GETFIELD, nms("EntityHuman").field("inventoryMenu")); // Get player's inventoryMenu
        super.visitMethodInsn(INVOKEVIRTUAL, nms("Container").method("sendAllDataToRemote")); // Send all inventory data to the player

        // }
        super.visitLabel(afterLabel);
    }
}
