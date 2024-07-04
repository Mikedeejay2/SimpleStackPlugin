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
@Transformer("1.20.6-1.21")
public class TransformItemSolidBucketUse extends MappedMethodVisitor {
    protected boolean visitedSetItemStart = false; // The start (before loading to stack) of the setItemInHand method
    protected boolean visitedAloadPlayer = false; // Loading the player for the setItemInHand method (prior to any arguments)
    protected boolean visitedGetItemStart = false; // The start (before loading to stack) of retrieving the item to set in the player's hand
    protected boolean visitedExtraInvoke = false; // Invoke between the start and the actual setItemInHand call
    protected boolean visitedSetItem = false; // The invoke for setItemInHand
    protected boolean appendedJumpFix = false; // Appending inventory update to prevent bucket from jumping in the inventory

    @Override
    public MappingEntry getMappingEntry() {
        return nms("SolidBucketItem").method("useOn");
    }

    @Override
    public void visitCode() {
        super.visitCode();
//        debugPrintString("Test of useOn method");
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        super.visitJumpInsn(opcode, label);
        if(!visitedSetItemStart && opcode == IFNULL) { // Unique instruction to 1.21+
            visitedSetItemStart = true;
        }
    }

    @Override
    public void visitVarInsn(int opcode, int varIndex) {
        super.visitVarInsn(opcode, varIndex);
        if(!visitedSetItemStart && opcode == ASTORE && varIndex == 4) { // Unique instruction to 1.20.6 or less
            visitedSetItemStart = true;
        } else if(visitedSetItemStart && !visitedAloadPlayer && opcode == ALOAD && varIndex == 3) { // Player index is 3 on all versions
            visitedAloadPlayer = true;
        } else if(!visitedGetItemStart && visitedAloadPlayer && opcode == ALOAD && varIndex == 4) { // For 1.20.6 or less, target loading player's hand
            visitedGetItemStart = true;
            appendInputArgs();
        }
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        if(!visitedSetItem && visitedExtraInvoke && opcode == INVOKEVIRTUAL) {
            visitedSetItem = true;
            appendCreateFilledResult();
        }
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        if(!visitedGetItemStart && visitedAloadPlayer && opcode == INVOKEVIRTUAL) { // For 1.21+, target inlined getHand
            visitedGetItemStart = true;
            appendInputArgs();
        } else if (!visitedExtraInvoke && visitedGetItemStart && opcode == INVOKEVIRTUAL) { // Flag the in between invoke (start of args > extra invoke > invoke call)
            visitedExtraInvoke = true;
        } else if(!appendedJumpFix && visitedSetItem) { // After setting the item, update the inventory for the client
            appendedJumpFix = true;
            appendInventoryUpdate();
        }
    }

    /**
     * Appends the call for ItemUtils.createFilledResult to perform proper stack checking, dropping if necessary, etc
     */
    private void appendCreateFilledResult() {
        visitInsn(ICONST_1); // Load true boolean for creative override argument
        visitMethodInsn(INVOKESTATIC, nms("ItemUtils").method("createFilledResult")); // Call createFilledResult (proper stack checking)
    }

    /**
     * Appends the first two arguments (before output stack argument) to the stack
     */
    private void appendInputArgs() {
        // Get the current item being held by the player
        super.visitVarInsn(ALOAD, 1); // Load UseOnContext
        super.visitMethodInsn(INVOKEVIRTUAL, nms("UseOnContext").method("getItemInHand")); // Get the item in hand
        super.visitInsn(DUP); // Duplicate the item on the stack
        super.visitInsn(ICONST_M1); // Load -1 to the stack (grow 1)
        super.visitMethodInsn(INVOKEVIRTUAL, nms("ItemStack").method("shrink")); // Grow the ItemStack by 1 (prevent use consuming twice)
        super.visitVarInsn(ALOAD, 3); // Load the player
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
