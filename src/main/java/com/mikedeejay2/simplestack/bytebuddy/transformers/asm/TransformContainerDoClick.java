package com.mikedeejay2.simplestack.bytebuddy.transformers.asm;

import com.mikedeejay2.simplestack.bytebuddy.MappedMethodVisitor;
import com.mikedeejay2.simplestack.bytebuddy.Transformer;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import static com.mikedeejay2.simplestack.bytebuddy.MappingsLookup.*;
import static org.objectweb.asm.Opcodes.*;

/**
 * Fixes swapping cursor items with items of different types and swapping overstacked items into the hotbar.
 *
 * @author Mikedeejay2
 */
@Transformer({
    "1.19", "1.19.1", "1.19.2", "1.19.3",
    "1.18", "1.18.1"
})
public class TransformContainerDoClick extends MappedMethodVisitor {
    protected boolean visitedIsSameItemSameTags = false;
    protected boolean appendedStackCheck1 = false;
    protected boolean appendedStackCheck2 = false;
    protected boolean appendedHotbarSwap = false;
    protected int countGetMaxStackSize = 0;

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
        if(!visitedIsSameItemSameTags && opcode == INVOKESTATIC && // Check starting reference method isSameItemSameTags
            owner.equals(nms("ItemStack").internalName()) &&
            name.equals(lastNms().method("isSameItemSameTags").name()) &&
            descriptor.equals(lastNmsMethod().descriptor())) {
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

    /**
     * Fixes swapping cursor items with items of different types. Add an if statement to ensure that items being swapped
     * aren't overstacked.
     *
     * @param label The label to jump to if false
     */
    public void appendStackSizeCheck(Label label) {
        super.visitVarInsn(ALOAD, 7); // Get slot's ItemStack
        super.visitMethodInsn(INVOKEVIRTUAL, nms("ItemStack").method("getCount")); // Get ItemStack count
        super.visitVarInsn(ALOAD, 7); // Get slot's ItemStack
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

        super.visitVarInsn(ALOAD, 7); // Load itemstack
        super.visitVarInsn(ALOAD, 9); // Load itemstack1
        super.visitJumpInsn(IF_ACMPEQ, exitLabel); // If they're the same, don't do this
        super.visitFrame(F_SAME, 0, null, 0, null);
        super.visitVarInsn(ALOAD, 5); // Load PlayerInventory
        super.visitVarInsn(ILOAD, 2); // Load button
        super.visitVarInsn(ALOAD, 7); // Load ItemStack
        // PlayerInventory, button, and ItemStack already exist on stack
        super.visitFieldInsn(GETSTATIC, "java/lang/Integer", "MAX_VALUE", "I"); // Get int max value
        super.visitMethodInsn(INVOKEVIRTUAL, nms("ItemStack").method("split")); // Split the max off of the ItemStack
        super.visitMethodInsn(INVOKEVIRTUAL, nms("PlayerInventory").method("setItem")); // Set split item to button slot

        super.visitVarInsn(ALOAD, 5); // Load PlayerInventory
        super.visitVarInsn(ALOAD, 7); // Load ItemStack
        super.visitMethodInsn(INVOKEVIRTUAL, nms("PlayerInventory").method("add")); // Add leftover ItemStack to inventory

        // If there are leftovers that won't fit into the inventory, throw them onto the ground
        Label insideLabel = new Label();

        super.visitJumpInsn(IFNE, exitLabel);
        super.visitLabel(insideLabel);
        super.visitVarInsn(ALOAD, 4); // Load EntityHuman
        super.visitVarInsn(ALOAD, 7); // Load ItemStack
        super.visitInsn(ICONST_0); // Load false (don't throw randomly)
        super.visitInsn(ICONST_1); // Load true (retain ownership of thrown item)
        super.visitMethodInsn(INVOKEVIRTUAL, nms("EntityHuman").method("drop")); // Drop the rest of the item
        super.visitInsn(POP); // Pop the resulting EntityItem

        super.visitLabel(exitLabel);
        super.visitFrame(F_SAME, 0, null, 0, null);
    }
}
