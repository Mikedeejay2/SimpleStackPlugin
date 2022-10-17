package com.mikedeejay2.simplestack.bytebuddy.transformers.asm.item;

import com.mikedeejay2.simplestack.bytebuddy.MappedMethodVisitor;
import com.mikedeejay2.simplestack.bytebuddy.Transformer;
import org.objectweb.asm.Label;

import static com.mikedeejay2.simplestack.bytebuddy.MappingsLookup.*;
import static org.objectweb.asm.Opcodes.*;

/**
 * Fixes stacked lava buckets from being allowed in furnace fuel. This fixes the furnace consuming the lava bucket but
 * not giving an empty bucket in return. This only fixes left-clicking.
 *
 * @author Mikedeejay2
 */
@Transformer({"1.19", "1.19.1", "1.19.2"})
public class TransformSlotFurnaceFuelIsBucket extends MappedMethodVisitor {
    @Override
    public MappingEntry getMappingEntry() {
        return nms("SlotFurnaceFuel").method("isBucket");
    }

    @Override
    public void visitCode() {
        super.visitCode();
//        System.out.println("isBucket");
        // Uncomment for debug message on visit code
//        debugPrintString("Test of isBucket method");
    }

    @Override
    public void visitInsn(int opcode) {
        if(opcode == IRETURN) { // Target return statement
            appendLavaBucketCheck();
        }
        super.visitInsn(opcode);
    }

    public void appendLavaBucketCheck() {
        Label trueLabel = new Label();
        Label falseLabel = new Label();
        Label returnLabel = new Label();
        super.visitJumpInsn(IFNE, trueLabel); // If is bucket, goto true label
        super.visitVarInsn(ALOAD, 0); // Load ItemStack
        super.visitFieldInsn(GETSTATIC, nms("Items").field("LAVA_BUCKET")); // Get Items.LAVA_BUCKET
        super.visitMethodInsn(INVOKEVIRTUAL, nms("ItemStack").method("is")); // ItemStack#is(Item)
        super.visitJumpInsn(IFEQ, falseLabel); // If not lava bucket, goto false label

        super.visitLabel(trueLabel); // True label
        super.visitFrame(F_SAME, 0, null, 0, null);
        super.visitInsn(ICONST_1); // Load 1 (true)
        super.visitJumpInsn(GOTO, returnLabel); // Goto the return label

        super.visitLabel(falseLabel); // False label
        super.visitFrame(F_SAME, 0, null, 0, null);
        super.visitInsn(ICONST_0); // Load 0 (false)

        super.visitLabel(returnLabel); // Return label
        super.visitFrame(F_SAME1, 0, null, 1, new Object[]{INTEGER});
        // Next instruction is IRETURN
    }
}
