package com.mikedeejay2.simplestack.bytecode.transformers.asm;

import com.mikedeejay2.simplestack.bytecode.MappingsLookup;
import com.mikedeejay2.simplestack.bytecode.MappedMethodVisitor;
import com.mikedeejay2.simplestack.bytecode.Transformer;
import org.objectweb.asm.Label;

import static com.mikedeejay2.simplestack.bytecode.MappingsLookup.*;
import static org.objectweb.asm.Opcodes.*;

/**
 * Fixes being able to overstack the cursor when picking up items from a result slot
 *
 * @author Mikedeejay2
 */
@Transformer({
    "1.20",
    "1.19", "1.19.1", "1.19.2", "1.19.3", "1.19.4",
    "1.18", "1.18.1", "1.18.2"
})
public class TransformContainerDoClickLambda extends MappedMethodVisitor {
    @Override
    public MappingsLookup.MappingEntry getMappingEntry() {
        return nms("Container").method("doClickLambda");
    }

    @Override
    public void visitCode() {
        super.visitCode();
        // Uncomment for debug message on visit code
//        System.out.println("doClickLambda");
//        debugPrintString("doClickLambda");

        appendOverstackCheck();
    }

    /**
     * Fixes being able to overstack the cursor when picking up items from a result slot
     */
    public void appendOverstackCheck() {
        super.visitVarInsn(ALOAD, 3); // Load ItemStack
        super.visitVarInsn(ASTORE, 4); // Save it to new local index
        super.visitVarInsn(ALOAD, 3); // Load ItemStack
        super.visitFieldInsn(GETSTATIC, "java/lang/Integer", "MAX_VALUE", "I"); // Get max int value
        super.visitMethodInsn(INVOKEVIRTUAL, nms("ItemStack").method("split")); // Split item stack to get max item stack if overstacked
        super.visitVarInsn(ASTORE, 3); // Set existing ItemStack to split ItemStack
        super.visitVarInsn(ALOAD, 2); // Load EntityHuman
        super.visitMethodInsn(INVOKEVIRTUAL, nms("EntityHuman").method("getInventory")); // Get the player's inventory
        super.visitVarInsn(ALOAD, 4); // Load the extra ItemStack (could be air)
        super.visitMethodInsn(INVOKEVIRTUAL, nms("PlayerInventory").method("add")); // Add extra to inventory

        // If there are leftovers that won't fit into the inventory, throw them onto the ground
        Label insideLabel = new Label();
        Label exitLabel = new Label();

        super.visitJumpInsn(IFNE, exitLabel); // If it couldn't add everything to the inventory
        super.visitLabel(insideLabel);
        super.visitVarInsn(ALOAD, 2); // Load EntityHuman
        super.visitVarInsn(ALOAD, 4); // Load ItemStack
        super.visitInsn(ICONST_0); // Load false (don't throw randomly)
        super.visitInsn(ICONST_0); // Load false (don't retain ownership of thrown item, this is default behavior)
        super.visitMethodInsn(INVOKEVIRTUAL, nms("EntityHuman").method("drop")); // Drop the rest of the item
        super.visitInsn(POP); // Pop the resulting EntityItem

        super.visitLabel(exitLabel);
        super.visitFrame(F_SAME, 0, null, 0, null);
    }
}
