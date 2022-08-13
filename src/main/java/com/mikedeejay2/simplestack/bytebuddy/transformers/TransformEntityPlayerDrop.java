package com.mikedeejay2.simplestack.bytebuddy.transformers;

import com.mikedeejay2.simplestack.bytebuddy.SimpleStackMethodVisitor;
import net.bytebuddy.jar.asm.Label;

import static com.mikedeejay2.simplestack.MappingsLookup.*;
import static net.bytebuddy.jar.asm.Opcodes.*;

public final class TransformEntityPlayerDrop extends SimpleStackMethodVisitor {
    @Override
    public MappingEntry getMappingEntry() {
        return nms("EntityPlayer").method("drop");
    }

    @Override
    public void visitCode() {
        super.visitCode();
//        System.out.println("Drop");
        // Uncomment for debug message on visit code
//        super.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
//        super.visitLdcInsn("Test of newer drop method");
//        super.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);

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
        super.visitVarInsn(ASTORE, 4); // Store new ItemStack to local index 5

        super.visitVarInsn(ALOAD, 0); // Load this
        super.visitVarInsn(ALOAD, 4); // Load split ItemStack
        super.visitVarInsn(ILOAD, 2); // Load flag boolean
        super.visitVarInsn(ILOAD, 3); // Load flag1 boolean
        super.visitMethodInsn(INVOKEVIRTUAL, nms("EntityPlayer").method("drop")); // Call the drop method recursively
        super.visitInsn(POP); // Pop the returned EntityItem from above call, we don't need it

        super.visitJumpInsn(GOTO, whileLabel); // Jump to start of while loop

        super.visitLabel(exitLabel); // Exit loop
        super.visitFrame(F_SAME, 0, null, 0, null);
    }
}
