package com.mikedeejay2.simplestack.bytecode.transformers.asm;

import com.mikedeejay2.simplestack.bytecode.MappedMethodVisitor;
import com.mikedeejay2.simplestack.bytecode.Transformer;
import org.objectweb.asm.Label;

import static com.mikedeejay2.simplestack.bytecode.MappingsLookup.*;
import static org.objectweb.asm.Opcodes.*;

/**
 * Fixes player dropping overstacked items. This ensures that items are split down to their max stack size.
 *
 * @author Mikedeejay2
 */
@Transformer({
    "1.20.4", "1.20.2", "1.20.1", "1.20",
    "1.19", "1.19.1", "1.19.2", "1.19.3", "1.19.4",
    "1.18", "1.18.1", "1.18.2"
})
public class TransformEntityPlayerDrop extends MappedMethodVisitor {
    @Override
    public MappingEntry getMappingEntry() {
        return nms("EntityPlayer").method("drop");
    }

    @Override
    public void visitCode() {
        super.visitCode();
//        System.out.println("Drop");
        // Uncomment for debug message on visit code
//        debugPrintString("Test of drop method");

        fixDropOverstacking();
    }

    /**
     * Fixes player dropping overstacked items. This ensures that items are split down to their max stack size.
     */
    private void fixDropOverstacking() {
        Label whileLabel = new Label(); // If part of while loop
        Label insideLabel = new Label(); // Label for inside of while loop
        Label exitLabel = new Label(); // Label for the exit of the while loop
        super.visitLabel(whileLabel);
        super.visitFrame(F_SAME, 0, null, 0, null);
        super.visitVarInsn(ALOAD, 1); // Load ItemStack
        super.visitMethodInsn(INVOKEVIRTUAL, nms("ItemStack").method("getCount")); // ItemStack#getCount()
        super.visitVarInsn(ALOAD, 1); // Load ItemStack
        super.visitMethodInsn(INVOKEVIRTUAL, lastNms().method("getMaxStackSize")); // ItemStack#getMaxStackSize()
        super.visitJumpInsn(IF_ICMPLE, exitLabel); // If count is greater than max stack size, loop

        super.visitLabel(insideLabel);
        super.visitVarInsn(ALOAD, 1); // Load ItemStack
        super.visitFieldInsn(GETSTATIC, "java/lang/Integer", "MAX_VALUE", "I"); // Get max Integer value
        super.visitMethodInsn(INVOKEVIRTUAL, lastNms().method("split")); // Split the max possible
        super.visitVarInsn(ASTORE, 6); // Store new ItemStack to local index 6

        super.visitVarInsn(ALOAD, 0); // Load this
        super.visitVarInsn(ALOAD, 6); // Load split ItemStack
        super.visitVarInsn(ILOAD, 2); // Load throwRandomly boolean
        super.visitVarInsn(ILOAD, 3); // Load retainOwnership boolean
        MappingEntry drop = nms("EntityPlayer").method("drop");
        if(drop.descriptor().contains("ZZZ)")) super.visitVarInsn(ILOAD, 4); // Load callDropEvent boolean (If running on paper servers)
        super.visitMethodInsn(INVOKEVIRTUAL, drop); // Call the drop method recursively
        super.visitInsn(POP); // Pop the returned EntityItem from above call, we don't need it

        super.visitJumpInsn(GOTO, whileLabel); // Jump to start of while loop

        super.visitLabel(exitLabel); // Exit loop
        super.visitFrame(F_SAME, 0, null, 0, null);
    }
}
