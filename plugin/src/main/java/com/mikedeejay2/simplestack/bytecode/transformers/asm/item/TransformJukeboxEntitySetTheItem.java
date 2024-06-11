package com.mikedeejay2.simplestack.bytecode.transformers.asm.item;

import com.mikedeejay2.simplestack.bytecode.MappedMethodVisitor;
import com.mikedeejay2.simplestack.bytecode.Transformer;
import com.mikedeejay2.simplestack.mappings.MappingEntry;

import static com.mikedeejay2.simplestack.mappings.MappingsLookup.nms;
import static org.objectweb.asm.Opcodes.*;

/**
 * Used to prevent jukeboxes from overstacking the record item and subsequently duplicate the record
 *
 * @author Mikedeejay2
 */
@Transformer("1.20.6")
public class TransformJukeboxEntitySetTheItem extends MappedMethodVisitor {
    private boolean visitedPutField = false;

    @Override
    public MappingEntry getMappingEntry() {
        return nms("JukeboxBlockEntity").method("setTheItem");
    }

    @Override
    public void visitCode() {
        super.visitCode();
//        System.out.println("setTheItem");
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        super.visitFieldInsn(opcode, owner, name, descriptor);
        if(!visitedPutField && opcode == PUTFIELD) {
            this.visitedPutField = true;
            appendFixStackSize();
        }
    }

    private void appendFixStackSize() {
        super.visitVarInsn(ALOAD, 1); // Load the ItemStack
        super.visitInsn(ICONST_1);
        super.visitMethodInsn(INVOKEVIRTUAL, nms("ItemStack").method("setCount"));
    }
}
