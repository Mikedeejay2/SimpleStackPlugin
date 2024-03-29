package com.mikedeejay2.simplestack.bytecode.transformers.asm.item;

import com.mikedeejay2.simplestack.bytecode.MappingsLookup;
import com.mikedeejay2.simplestack.bytecode.MappedMethodVisitor;
import com.mikedeejay2.simplestack.bytecode.Transformer;

import static org.objectweb.asm.Opcodes.*;
import static com.mikedeejay2.simplestack.bytecode.MappingsLookup.*;

/**
 * Fixes throwing all tridents in a stack at once.
 *
 * @author Mikedeejay2
 */
@Transformer("1.18-1.20.4")
public class TransformItemTridentReleaseUsing extends MappedMethodVisitor {
    private boolean visitedNew = false;

    @Override
    public MappingsLookup.MappingEntry getMappingEntry() {
        return nms("ItemTrident").method("releaseUsing");
    }

    @Override
    public void visitCode() {
        super.visitCode();

//        System.out.println("Trident");
//        debugPrintString("Threw trident");
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
        if(!visitedNew && type.equals(nms("EntityThrownTrident").internalName())) {
            visitedNew = true;
            fixTridentThrow();
        }
        super.visitTypeInsn(opcode, type);
    }

    public void fixTridentThrow() {
        super.visitVarInsn(ALOAD, 1); // Load ItemStack
        super.visitInsn(ICONST_1); // Load int 1
        super.visitMethodInsn(INVOKEVIRTUAL, nms("ItemStack").method("split"));
        super.visitVarInsn(ASTORE, 1); // Store split ItemStack
    }
}
