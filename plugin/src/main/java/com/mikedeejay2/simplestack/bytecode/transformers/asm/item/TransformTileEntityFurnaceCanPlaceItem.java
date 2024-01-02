package com.mikedeejay2.simplestack.bytecode.transformers.asm.item;

import com.mikedeejay2.simplestack.bytecode.MappedMethodVisitor;
import com.mikedeejay2.simplestack.bytecode.Transformer;
import org.objectweb.asm.Label;

import static com.mikedeejay2.simplestack.bytecode.MappingsLookup.*;
import static org.objectweb.asm.Opcodes.*;

/**
 * Fixes hoppers overstacking lava buckets in the fuel slot of a furnace. This ensures that only one lava bucket can be
 * inside the fuel slot. If multiple lava buckets are stacked together, the empty bucket will not be returned.
 *
 * @author Mikedeejay2
 */
@Transformer("1.18-1.20.4")
public class TransformTileEntityFurnaceCanPlaceItem extends MappedMethodVisitor {
    private boolean visitedAstore = false;
    private int jumpInsnCount = 0;
    private Label falseLabel = null;
    private Label trueLabel = null;
    private boolean visitedTrueLabel = false;
    private boolean visitedTrueFrame = false;

    @Override
    public MappingEntry getMappingEntry() {
        return nms("TileEntityFurnace").method("canPlaceItem");
    }

    @Override
    public void visitCode() {
        super.visitCode();
//        System.out.println("canPlaceItem");
//        debugPrintString("canPlaceItem");
    }

    @Override
    public void visitVarInsn(int opcode, int varIndex) {
        super.visitVarInsn(opcode, varIndex);
        if(!visitedAstore && opcode == ASTORE && varIndex == 3) {
            visitedAstore = true;
        }
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        super.visitJumpInsn(opcode, label);
        if(visitedAstore && (opcode == IFNE || opcode == IFEQ)) { // Count the jumps
            ++jumpInsnCount;
            if(jumpInsnCount == 1) { // The first jump points to true on both paper and spigot compilations
                trueLabel = label;
            } else if(jumpInsnCount == 2) { // The second jump points to false on both paper and spigot compilations
                falseLabel = label;
            }
        }
    }

    @Override
    public void visitLabel(Label label) {
        super.visitLabel(label);
        if(label == trueLabel) {
            visitedTrueLabel = true;
        }
    }

    @Override
    public void visitFrame(int type, int numLocal, Object[] local, int numStack, Object[] stack) {
        super.visitFrame(type, numLocal, local, numStack, stack);
        if(!visitedTrueFrame && visitedTrueLabel) {
            visitedTrueFrame = true;
            appendLavaBucketFix();
        }
    }

    /**
     * AND the existing if statement with
     * <code>Itemstack.is(Items.LAVA_BUCKET) && !itemstack1.is(Items.LAVA_BUCKET)</code>.
     * Fixes overstacking lava buckets
     */
    private void appendLavaBucketFix() {
        Label newTrueLabel = new Label();
        super.visitVarInsn(ALOAD, 2); // Load ItemStack
        super.visitFieldInsn(GETSTATIC, nms("Items").field("LAVA_BUCKET")); // Get Items.LAVA_BUCKET
        super.visitMethodInsn(INVOKEVIRTUAL, nms("ItemStack").method("is")); // ItemStack#is(Item)
        super.visitJumpInsn(IFEQ, newTrueLabel); // If not lava bucket, goto true label

        super.visitVarInsn(ALOAD, 3); // Load itemstack1
        super.visitFieldInsn(GETSTATIC, nms("Items").field("LAVA_BUCKET")); // Get Items.LAVA_BUCKET
        super.visitMethodInsn(INVOKEVIRTUAL, nms("ItemStack").method("is")); // ItemStack#is(Item)
        super.visitJumpInsn(IFNE, falseLabel); // If lava bucket, goto false label

        super.visitLabel(newTrueLabel);
        super.visitFrame(F_SAME, 0, null, 0, null);
    }
}
