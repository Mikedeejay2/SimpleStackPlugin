package com.mikedeejay2.simplestack.bytecode.transformers.asm.item;

import com.mikedeejay2.simplestack.bytecode.MappedMethodVisitor;
import com.mikedeejay2.simplestack.bytecode.Transformer;
import com.mikedeejay2.simplestack.mappings.MappingEntry;
import org.objectweb.asm.Label;

import static com.mikedeejay2.simplestack.mappings.MappingsLookup.*;
import static org.objectweb.asm.Opcodes.*;

/**
 * Fixes stacked lava buckets from being allowed in furnace fuel. This fixes the furnace consuming the lava bucket but
 * not giving an empty bucket in return. This only fixes left-clicking.
 *
 * @author Mikedeejay2
 */
@Transformer("1.18-1.21.6")
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
        super.visitInsn(ICONST_1); // Load 1 (true)
        super.visitJumpInsn(GOTO, returnLabel); // Goto the return label

        super.visitLabel(falseLabel); // False label
        super.visitInsn(ICONST_0); // Load 0 (false)

        super.visitLabel(returnLabel); // Return label
        // Next instruction is IRETURN
    }
}
