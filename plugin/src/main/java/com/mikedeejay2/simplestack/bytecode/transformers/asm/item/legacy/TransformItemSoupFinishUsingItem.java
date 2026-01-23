package com.mikedeejay2.simplestack.bytecode.transformers.asm.item.legacy;

import com.mikedeejay2.simplestack.bytecode.MappedMethodVisitor;
import com.mikedeejay2.simplestack.bytecode.Transformer;
import com.mikedeejay2.simplestack.mappings.MappingEntry;
import org.objectweb.asm.Label;

import static com.mikedeejay2.simplestack.mappings.MappingsLookup.*;
import static org.objectweb.asm.Opcodes.*;

/**
 * Fixes stacked soups from being replaced by a bowl upon use.
 *
 * @author Mikedeejay2
 */
@Transformer("1.18-1.20.6")
public class TransformItemSoupFinishUsingItem extends MappedMethodVisitor {
    protected int stackIndex = 4;
    protected boolean visitedNew = false; // New ItemStack, separate from frame

    @Override
    public MappingEntry getMappingEntry() {
        return nms("ItemSoup").method("finishUsingItem");
    }

    @Override
    public String[] getValidationMarkers() {
        return new String[] {"visitedNew", "appendStackedSoupFix"};
    }

    @Override
    public void visitCode() {
        super.visitCode();
//        debugPrintString("Test of finishUsingItem method in ItemSoup");
    }

    @Override
    public void visitVarInsn(int opcode, int varIndex) {
        super.visitVarInsn(opcode, varIndex);
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
        if(!visitedNew && opcode == NEW && type.equals(nms("ItemStack").internalName())) { // Target new ItemStack() invocation
            visitedNew = true;
            this.marker("visitedNew");
            appendStackedSoupFix();
        }
        super.visitTypeInsn(opcode, type);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
    }

    /**
     * Fixes stacked soups from being replaced by a bowl upon use.
     * <p>
     * 1.19.2 fix - Store EntityHuman in index 6, and ItemStack in index 7. In Minecraft 1.19, locals were used up to 4,
     * in 1.19.2, locals were used up to 5 instead because of NBTTagCompound in index 5. To fix this, new stores have
     * been increased by one index.
     */
    private void appendStackedSoupFix() {
        this.marker("appendStackedSoupFix");
        final Label emptyBowlLabel = new Label();

        // Get EntityHuman from EntityLiving
        super.visitVarInsn(ALOAD, 3); // Load EntityLiving
        super.visitTypeInsn(INSTANCEOF, nms("EntityHuman").internalName()); // Check is EntityLiving is EntityHuman
        super.visitJumpInsn(IFEQ, emptyBowlLabel); // If not EntityHuman, goto default behavior

        super.visitVarInsn(ALOAD, 3); // Load EntityLiving
        super.visitTypeInsn(CHECKCAST, nms("EntityHuman").internalName()); // Cast to EntityHuman
        super.visitVarInsn(ASTORE, 6); // Store EntityHuman in local index 6

        // ItemStack is already shrunk by 1, check if it's empty
        super.visitVarInsn(ALOAD, stackIndex); // Load ItemStack
        super.visitMethodInsn(INVOKEVIRTUAL, nms("ItemStack").method("isEmpty")); // Get whether ItemStack is empty

        Label ifNotDropLabel = new Label();
        super.visitJumpInsn(IFNE, emptyBowlLabel); // If it is empty, jump to empty bowl

        // Get PlayerInventory
        super.visitVarInsn(ALOAD, 6); // Load EntityHuman
        super.visitMethodInsn(INVOKEVIRTUAL, nms("EntityHuman").method("getInventory"));
        // New empty bowl ItemStack
        super.visitTypeInsn(NEW, nms("ItemStack").internalName()); // Create new ItemStack
        super.visitInsn(DUP); // Duplicate this ItemStack on the stack
        super.visitFieldInsn(GETSTATIC, nms("Items").field("BOWL")); // Get Bowl material
        super.visitMethodInsn(INVOKESPECIAL, nms("ItemStack").method("<init>")); // Call the ItemStack's constructor
        super.visitVarInsn(ASTORE, 7); // Store the new ItemStack to local index 7
        super.visitVarInsn(ALOAD, 7); // Load the new ItemStack
        // Add new bowl to inventory
        super.visitMethodInsn(INVOKEVIRTUAL, nms("PlayerInventory").method("add")); // Attempt to add bowl to inventory
        // If it failed, drop on ground
        super.visitJumpInsn(IFNE, ifNotDropLabel); // If no items need to be dropped, bypass drop method

        super.visitVarInsn(ALOAD, 6); // Load EntityHuman
        super.visitVarInsn(ALOAD, 7); // Load the new ItemStack (empty bowl)
        super.visitInsn(ICONST_0); // Load false (don't throw randomly)
        super.visitInsn(ICONST_1); // Load true (retain ownership of thrown item)
        super.visitMethodInsn(INVOKEVIRTUAL, nms("EntityHuman").method("drop")); // Drop the rest of the item
        super.visitInsn(POP); // Pop the resulting EntityItem

        super.visitLabel(ifNotDropLabel); // Exit drop if statement

        // Get ItemStack
        super.visitVarInsn(ALOAD, stackIndex); // Load ItemStack
        // Goto the return label
        super.visitInsn(ARETURN); // Return the ItemStack
        super.visitLabel(emptyBowlLabel);
    }
}
