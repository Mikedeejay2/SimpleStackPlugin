package com.mikedeejay2.simplestack.bytebuddy.transformers.asm.item;

import com.mikedeejay2.simplestack.bytebuddy.MappedMethodVisitor;
import com.mikedeejay2.simplestack.bytebuddy.MappingsLookup;
import com.mikedeejay2.simplestack.bytebuddy.Transformer;
import org.objectweb.asm.Label;

import static com.mikedeejay2.simplestack.bytebuddy.MappingsLookup.*;
import static org.objectweb.asm.Opcodes.*;

//@Transformer({"1.19"})
public class TransformContainerFurnaceQuickMoveStack extends MappedMethodVisitor {
    private boolean visitedIsFuel = false;
    private boolean visitedIfeq = false;
    private boolean visitedAload = false;
    private boolean visitedLabel = false;
    private final Label actualLabel = new Label();

    @Override
    public MappingsLookup.MappingEntry getMappingEntry() {
        return nms("ContainerFurnace").method("quickMoveStack");
    }

    @Override
    public void visitCode() {
        super.visitCode();

        System.out.println("quickMoveStack");
        debugPrintString("quickMoveStack");
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
    public void visitJumpInsn(int opcode, Label label) {
        if(visitedIsFuel && !visitedIfeq && opcode == IFEQ) { // Target isFuel if statement
            visitedIfeq = true;
        } else if(visitedAload && !visitedLabel && opcode == IFNE) { // Target quickMoveStack if statement
            visitedLabel = true;
            appendIfStatementFix(label);
            return;
        }
        super.visitJumpInsn(opcode, label);
    }

    /**
     * Makes the if statement to return an empty item stack be called if moving a lava bucket.
     *
     * @param label
     */
    private void appendIfStatementFix(Label label) {
        super.visitJumpInsn(IFEQ, actualLabel); // Invert statement so that if false, enter if statement

        super.visitVarInsn(ALOAD, 5); // Load ItemStack1
        super.visitFieldInsn(GETSTATIC, nms("Items").field("LAVA_BUCKET")); // Get Items.LAVA_BUCKET
        super.visitMethodInsn(INVOKEVIRTUAL, nms("ItemStack").method("is")); // ItemStack#is(Item)
        super.visitJumpInsn(IFEQ, label); // If not lava bucket, goto exit

        super.visitLabel(actualLabel);
    }

    @Override
    public void visitVarInsn(int opcode, int varIndex) {
        if(!visitedAload && visitedIfeq && opcode == ALOAD && varIndex == 5) { // Target loading of ItemStack1 upon moveItemStackTo method
            visitedAload = true;

            Label notLavaBucketLabel = new Label();
            Label exitLabel = new Label();
            super.visitVarInsn(ALOAD, 5); // Load ItemStack1
            super.visitFieldInsn(GETSTATIC, nms("Items").field("LAVA_BUCKET")); // Get Items.LAVA_BUCKET
            super.visitMethodInsn(INVOKEVIRTUAL, nms("ItemStack").method("is")); // ItemStack#is(Item)
            super.visitJumpInsn(IFEQ, notLavaBucketLabel); // If not lava bucket, bypass shrink

            // Shrink by one
            super.visitVarInsn(ALOAD, 5); // Load ItemStack1
            super.visitInsn(ICONST_1); // Load 1 (to shrink by)
            super.visitMethodInsn(INVOKEVIRTUAL, lastNms().method("split")); // Shrink by 64
            super.visitJumpInsn(GOTO, exitLabel);

            // If not a lava bucket, load the ItemStack
            super.visitLabel(notLavaBucketLabel);
            super.visitFrame(F_SAME1, 0, null, 1, new Object[] {nms("ContainerFurnace").internalName()}); // Has "this" on the stack
            super.visitVarInsn(ALOAD, 5); // Load ItemStack1

            super.visitLabel(exitLabel);
            super.visitFrame(F_FULL, 6, new Object[] { // Has two values on the stack
                nms("ContainerFurnace").internalName(),
                nms("EntityHuman").internalName(),
                INTEGER,
                nms("ItemStack").internalName(),
                nms("Slot").internalName(),
                nms("ItemStack").internalName()
            }, 2, new Object[] {
                nms("ContainerFurnace").internalName(),
                nms("ItemStack").internalName()
            });
            return;
        }
        super.visitVarInsn(opcode, varIndex);
    }
}
