package com.mikedeejay2.simplestack.bytecode.transformers.asm;

import com.mikedeejay2.simplestack.bytecode.MappedMethodVisitor;
import com.mikedeejay2.simplestack.bytecode.Transformer;
import com.mikedeejay2.simplestack.mappings.MappingEntry;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import static com.mikedeejay2.simplestack.mappings.MappingsLookup.*;
import static org.objectweb.asm.Opcodes.*;

/**
 * Fixes swapping cursor items with items of different types and swapping overstacked items into the hotbar.
 *
 * @author Mikedeejay2
 */
@Transformer("1.18-1.21.4")
public class TransformContainerDoClick extends MappedMethodVisitor {
    protected boolean visitedIsSameItemSameTags = false; // Reference position method for appendStackSizeCheck appends
    protected boolean appendedStackCheck1 = false; // Stack size check 1
    protected boolean appendedStackCheck2 = false; // Stack size check 2
    protected boolean appendedHotbarSwap = false; // Whether the first hotbar swap fix has been appended
    protected boolean visitedIsEmpty1 = false; // Reference for hotbarItemIdx1
    protected boolean visitedIsEmpty2 = false; // Reference for hotbarItemIdx1
    protected int countGetMaxStackSize = 0; // Reference counter
    protected int stackTempAloadIdx = -1; // Reference for stackItemStackIdx
    protected int stackItemIdx = -1; // ItemStack index used in appendStackSizeCheck
    protected int hotbarItemIdx1 = -1; // ItemStack index used in appendHotbarSwap
    protected int hotbarItemIdx2 = -1; // ItemStack index used in appendHotbarSwap

    @Override
    public MappingEntry getMappingEntry() {
        return nms("Container").method("doClick");
    }

    @Override
    public void visitCode() {
        super.visitCode();
//        System.out.println("doClick");
        // Uncomment for debug message on visit code
//        debugPrintObject(3); // Print InventoryClickType
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        if(!visitedIsSameItemSameTags && opcode == INVOKESTATIC && // Check starting reference method isSameItemSameComponents
            equalsMapping(owner, name, descriptor, nms("ItemStack").method("isSameItemSameComponents"))) {
            this.visitedIsSameItemSameTags = true;
        } else if(!appendedHotbarSwap && opcode == INVOKEVIRTUAL && // Hotbar swap point 1
            equalsMapping(owner, name, descriptor, nms("PlayerInventory").method("setItem"))) {
            appendHotbarSwap();
            this.appendedHotbarSwap = true;
            return;
        } else if(appendedHotbarSwap && opcode == INVOKEVIRTUAL && // Count Slot#getMaxStackSize(ItemStack) methods as next reference point
            equalsMapping(owner, name, descriptor, nms("Slot").method("getMaxStackSize1"))) {
            ++countGetMaxStackSize;
        } else if(countGetMaxStackSize == 2 && opcode == INVOKEVIRTUAL && // Hotbar swap point 2
            equalsMapping(owner, name, descriptor, nms("PlayerInventory").method("setItem"))) {
            appendHotbarSwap();
            return;
        } else if(visitedIsSameItemSameTags && !visitedIsEmpty1 && opcode == INVOKEVIRTUAL && // Reference point to obtain hotbarItemIdx1
            equalsMapping(owner, name, descriptor, nms("ItemStack").method("isEmpty"))) {
            visitedIsEmpty1 = true;
        } else if(visitedIsEmpty1 && !visitedIsEmpty2 && opcode == INVOKEVIRTUAL && // Reference point to obtain hotbarItemIdx2
            equalsMapping(owner, name, descriptor, nms("ItemStack").method("isEmpty"))) {
            visitedIsEmpty2 = true;
        }
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        super.visitJumpInsn(opcode, label);
        if(!appendedStackCheck1 && visitedIsSameItemSameTags && opcode == Opcodes.IF_ICMPGT) { // Get the if statement "<=" to target second cursor swap action
            appendStackSizeCheck(label); // Add additional stack size check
            this.appendedStackCheck1 = true;
        } else if(!appendedStackCheck2 && visitedIsSameItemSameTags && opcode == IFEQ) { // Get the if statement to target first cursor swap action
            appendStackSizeCheck(label); // Add additional stack size check
            this.appendedStackCheck2 = true;
        }
    }

    @Override
    public void visitVarInsn(int opcode, int varIndex) {
        if(!visitedIsSameItemSameTags && opcode == ALOAD) { // Record previous ALOAD indexes for reference later
            stackItemIdx = stackTempAloadIdx;
            stackTempAloadIdx = varIndex;
        }
        if(!visitedIsEmpty1 && opcode == ALOAD) {
            hotbarItemIdx1 = varIndex;
        }
        if(!visitedIsEmpty2 && opcode == ALOAD) {
            hotbarItemIdx2 = varIndex;
        }
        super.visitVarInsn(opcode, varIndex);
    }

    /**
     * Fixes swapping cursor items with items of different types. Add an if statement to ensure that items being swapped
     * aren't overstacked.
     *
     * @param label The label to jump to if false
     */
    public void appendStackSizeCheck(Label label) {
        super.visitVarInsn(ALOAD, stackItemIdx); // Get slot's ItemStack
        super.visitMethodInsn(INVOKEVIRTUAL, nms("ItemStack").method("getCount")); // Get ItemStack count
        super.visitVarInsn(ALOAD, stackItemIdx); // Get slot's ItemStack
        super.visitMethodInsn(INVOKEVIRTUAL, nms("ItemStack").method("getMaxStackSize")); // Get ItemStack max stack size
        super.visitJumpInsn(IF_ICMPGT, label); // If count is less than or equal to getMaxStackSize, continue
    }

    /**
     * Fixes swapping overstacked items into the hotbar. Injected on a PlayerInventory#setItem call.
     */
    public void appendHotbarSwap() {
        Label exitLabel = new Label();

        super.visitInsn(POP); // Pop ItemStack
        super.visitInsn(POP); // Pop button
        super.visitInsn(POP); // Pop PlayerInventory

        super.visitVarInsn(ALOAD, hotbarItemIdx1); // Load itemstack
        super.visitVarInsn(ALOAD, hotbarItemIdx2); // Load itemstack1
        super.visitJumpInsn(IF_ACMPEQ, exitLabel); // If they're the same, don't do this
        super.visitVarInsn(ALOAD, 5); // Load PlayerInventory
        super.visitVarInsn(ILOAD, 2); // Load button
        super.visitVarInsn(ALOAD, hotbarItemIdx2); // Load ItemStack
        // PlayerInventory, button, and ItemStack already exist on stack
        super.visitFieldInsn(GETSTATIC, "java/lang/Integer", "MAX_VALUE", "I"); // Get int max value
        super.visitMethodInsn(INVOKEVIRTUAL, nms("ItemStack").method("split")); // Split the max off of the ItemStack
        super.visitMethodInsn(INVOKEVIRTUAL, nms("PlayerInventory").method("setItem")); // Set split item to button slot

        super.visitVarInsn(ALOAD, 5); // Load PlayerInventory
        super.visitVarInsn(ALOAD, hotbarItemIdx2); // Load ItemStack
        super.visitMethodInsn(INVOKEVIRTUAL, nms("PlayerInventory").method("add")); // Add leftover ItemStack to inventory

        // If there are leftovers that won't fit into the inventory, throw them onto the ground
        Label insideLabel = new Label();

        super.visitJumpInsn(IFNE, exitLabel);
        super.visitLabel(insideLabel);
        super.visitVarInsn(ALOAD, 4); // Load EntityHuman
        super.visitVarInsn(ALOAD, hotbarItemIdx2); // Load ItemStack
        super.visitInsn(ICONST_0); // Load false (don't throw randomly)
        super.visitInsn(ICONST_1); // Load true (retain ownership of thrown item)
        super.visitMethodInsn(INVOKEVIRTUAL, nms("EntityHuman").method("drop")); // Drop the rest of the item
        super.visitInsn(POP); // Pop the resulting EntityItem

        super.visitLabel(exitLabel);
    }
}
