package com.mikedeejay2.simplestack.bytebuddy.transformers.asm.item;

import com.mikedeejay2.simplestack.bytebuddy.MappedMethodVisitor;
import com.mikedeejay2.simplestack.bytebuddy.MappingsLookup;
import com.mikedeejay2.simplestack.bytebuddy.Transformer;
import org.objectweb.asm.Label;

import static com.mikedeejay2.simplestack.bytebuddy.MappingsLookup.*;
import static org.objectweb.asm.Opcodes.*;

/**
 * Fixes shift clicking lava buckets into a fuel slot of a furnace. This ensures that only one lava bucket can be
 * inside the fuel slot. If multiple lava buckets are stacked together, the empty bucket will not be returned.
 *
 * @author Mikedeejay2
 */
@Transformer({"1.19", "1.19.1", "1.19.2"})
public class TransformContainerFurnaceQuickMoveStack extends MappedMethodVisitor {
    private boolean visitedIsFuel = false;
    private boolean visitedEntranceLabel = false;
    private boolean visitedExitLabel = false;
    private final Label newExitLabel = new Label(); // Represents label that exits the isFuel if statement, not the next else if
    private Label actualExitLabel; // Represents the actual exit label, pulled from quickMoveStack jump, not the next else if

    @Override
    public MappingsLookup.MappingEntry getMappingEntry() {
        return nms("ContainerFurnace").method("quickMoveStack");
    }

    @Override
    public void visitCode() {
        super.visitCode();

//        System.out.println("quickMoveStack");
//        debugPrintString("quickMoveStack");
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        if(!visitedIsFuel && opcode == INVOKEVIRTUAL &&
            equalsMapping(owner, name, descriptor, nms("ContainerFurnace").method("isFuel"))) {
            visitedIsFuel = true;
        }
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
    }

    @Override
    public void visitLabel(Label label) {
        if(!visitedExitLabel && visitedEntranceLabel && actualExitLabel != null && label == actualExitLabel) { // Target actual exit label. This is the exit from isFuel and is not the next else if
            visitedExitLabel = true;
            super.visitLabel(newExitLabel); // Add the new exit label to the existing exit label
        }

        super.visitLabel(label);

        if(!visitedEntranceLabel && visitedIsFuel) { // Target the label after the isFuel statement, inside isFuel if statement
            visitedEntranceLabel = true;
            appendIsLavaBucketFix();
        }
    }

    public void visitJumpInsn(int opcode, Label label) {
        if(actualExitLabel == null && visitedEntranceLabel && opcode == IFNE) { // Target quickMoveStack if statement
            actualExitLabel = label;
        }
        super.visitJumpInsn(opcode, label);
    }

    /**
     * Fixes shift clicking lava buckets into a fuel slot of a furnace. This ensures that only one lava bucket can be
     * inside the fuel slot. If multiple lava buckets are stacked together, the empty bucket will not be returned.
     */
    private void appendIsLavaBucketFix() {
        Label notLavaBucketLabel = new Label();
        // if(itemstack1.is(Items.LAVA_BUCKET))
        super.visitVarInsn(ALOAD, 5); // Load itemstack1
        super.visitFieldInsn(GETSTATIC, nms("Items").field("LAVA_BUCKET")); // Get Items.LAVA_BUCKET
        super.visitMethodInsn(INVOKEVIRTUAL, nms("ItemStack").method("is")); // ItemStack#is(Item)
        super.visitJumpInsn(IFEQ, notLavaBucketLabel); // If not lava bucket, goto false label

        super.visitVarInsn(ALOAD, 0); // Load "this" ContainerFurnace
        super.visitInsn(ICONST_1); // Get slot index 1
        super.visitMethodInsn(INVOKEVIRTUAL, nms("Container").method("getSlot")); // Get the slot at index 1
        super.visitMethodInsn(INVOKEVIRTUAL, nms("Slot").method("getItem")); // Get the item from that slot
        super.visitMethodInsn(INVOKEVIRTUAL, nms("ItemStack").method("isEmpty")); // Get whether the item is empty
        super.visitJumpInsn(IFEQ, newExitLabel); // If not empty, goto false label

        // ItemStack splitStack = itemstack1.split(1); // Only one lava bucket should be moved
        super.visitVarInsn(ALOAD, 5); // Load itemstack1
        super.visitInsn(ICONST_1); // Load int 1 (split by 1)
        super.visitMethodInsn(INVOKEVIRTUAL, nms("ItemStack").method("split")); // Split itemstack1 by one
        super.visitVarInsn(ASTORE, 6); // Store split stack to index 6

        // if(!this.moveItemStackTo(splitStack, 1, 2, false))
        super.visitVarInsn(ALOAD, 0); // Load "this" ContainerFurnace
        super.visitVarInsn(ALOAD, 6); // Load splitStack
        super.visitInsn(ICONST_1); // Load int 1
        super.visitInsn(ICONST_2); // Load int 2
        super.visitInsn(ICONST_0); // Load int 0 (false)
        super.visitMethodInsn(INVOKEVIRTUAL, nms("ContainerFurnace").method("moveItemStackTo")); // Invoke method moveItemStackTo

        // Return empty regardless of moveItemStackTo state, this will break the quickMoveStack loop
        // return ItemStack.EMPTY;
        super.visitFieldInsn(GETSTATIC, nms("ItemStack").field("EMPTY")); // Get ItemStack.EMPTY
        super.visitInsn(ARETURN); // Return empty item, this method can do no more

        super.visitLabel(notLavaBucketLabel); // Below this is for regular item handling, not lava buckets
        super.visitFrame(F_SAME, 0, null, 0, null);
    }
}
