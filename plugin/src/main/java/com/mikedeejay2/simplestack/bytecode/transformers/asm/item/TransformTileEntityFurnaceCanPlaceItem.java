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
@Transformer({
    "1.20.4", "1.20.2", "1.20.1", "1.20",
    "1.19", "1.19.1", "1.19.2", "1.19.3", "1.19.4",
    "1.18", "1.18.1", "1.18.2"
})
public class TransformTileEntityFurnaceCanPlaceItem extends MappedMethodVisitor {
    private boolean visitedBucket = false;
    private boolean visitedTrueFrame = false;
    private boolean visitedFalseLabel = false;
    private final Label newFalseLabel = new Label();

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
    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        if(!visitedBucket && opcode == GETSTATIC &&
            equalsMapping(owner, name, descriptor, nms("Items").field("BUCKET"))) {
            visitedBucket = true;
        }
        super.visitFieldInsn(opcode, owner, name, descriptor);
    }

    @Override
    public void visitLabel(Label label) {
        super.visitLabel(label);
        if(!visitedFalseLabel && visitedTrueFrame) {
            visitedFalseLabel = true;
            super.visitLabel(newFalseLabel);
        }
    }

    @Override
    public void visitFrame(int type, int numLocal, Object[] local, int numStack, Object[] stack) {
        super.visitFrame(type, numLocal, local, numStack, stack);
        if(!visitedTrueFrame && visitedBucket) {
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
        Label trueLabel = new Label();
        super.visitVarInsn(ALOAD, 2); // Load ItemStack
        super.visitFieldInsn(GETSTATIC, nms("Items").field("LAVA_BUCKET")); // Get Items.LAVA_BUCKET
        super.visitMethodInsn(INVOKEVIRTUAL, nms("ItemStack").method("is")); // ItemStack#is(Item)
        super.visitJumpInsn(IFEQ, trueLabel); // If not lava bucket, goto true label

        super.visitVarInsn(ALOAD, 3); // Load itemstack1
        super.visitFieldInsn(GETSTATIC, nms("Items").field("LAVA_BUCKET")); // Get Items.LAVA_BUCKET
        super.visitMethodInsn(INVOKEVIRTUAL, nms("ItemStack").method("is")); // ItemStack#is(Item)
        super.visitJumpInsn(IFNE, newFalseLabel); // If lava bucket, goto false label

        super.visitLabel(trueLabel);
        super.visitFrame(F_SAME, 0, null, 0, null);
    }
}
