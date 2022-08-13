package com.mikedeejay2.simplestack.bytebuddy.transformers;

import com.mikedeejay2.simplestack.MappingsLookup;
import com.mikedeejay2.simplestack.bytebuddy.SimpleStackMethodVisitor;
import net.bytebuddy.jar.asm.Label;

import static net.bytebuddy.jar.asm.Opcodes.*;
import static com.mikedeejay2.simplestack.MappingsLookup.*;

public class TransformContainerWorkbenchQuickMoveStack extends SimpleStackMethodVisitor {
    private Label exitLabel;
    private boolean fixedDrop = false;
    private boolean exitedIf = false;

    @Override
    public MappingsLookup.MappingEntry getMappingEntry() {
        return nms("ContainerWorkbench").method("quickMoveStack");
    }

    @Override
    public void visitCode() {
        super.visitCode();
//        debugPrintString("Test of quickMoveStack method");
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        if(opcode == INVOKEVIRTUAL && // Target drop method invocation
            equalsMapping(owner, name, descriptor, nms("EntityHuman").method("drop2args"))) {
            System.out.println("test");
            super.visitInsn(POP); // Pop EntityHuman
            super.visitInsn(POP); // Pop ItemStack
            super.visitInsn(POP); // Pop int 0
            super.visitVarInsn(ALOAD, 1); // Load EntityHuman
            super.visitMethodInsn(INVOKEVIRTUAL, nms("EntityHuman").method("getInventory")); // Get the player's inventory
            super.visitVarInsn(ALOAD, 5); // Load ItemStack
            super.visitMethodInsn(INVOKEVIRTUAL, nms("PlayerInventory").method("add")); // Add extra to inventory

            // If there are leftovers that won't fit into the inventory, throw them onto the ground
            Label insideLabel = new Label();
            exitLabel = new Label();

            super.visitJumpInsn(IFNE, exitLabel); // If it couldn't add everything to the inventory
            super.visitLabel(insideLabel);

            super.visitVarInsn(ALOAD, 1); // Load EntityHuman
            super.visitVarInsn(ALOAD, 5); // Load ItemStack
            super.visitInsn(ICONST_0); // Load int 0
            // Below this is the original drop method call.
            fixedDrop = true;
        }
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
    }

    @Override
    public void visitInsn(int opcode) {
        super.visitInsn(opcode);
        if(fixedDrop && opcode == POP && !exitedIf) {
            System.out.println("Test2");
            super.visitLabel(exitLabel);
//            super.visitFrame(F_SAME, 0, null, 0, null);
            exitedIf = true;
        }
    }
}
