package com.mikedeejay2.simplestack.bytecode.transformers.asm.item;

import com.mikedeejay2.mikedeejay2lib.util.version.MinecraftVersion;
import com.mikedeejay2.simplestack.bytecode.MappedMethodVisitor;
import com.mikedeejay2.simplestack.bytecode.Transformer;
import com.mikedeejay2.simplestack.mappings.MappingEntry;
import org.objectweb.asm.Label;

import static com.mikedeejay2.simplestack.mappings.MappingsLookup.nms;
import static org.objectweb.asm.Opcodes.*;

/**
 * Used to prevent jukeboxes from overstacking the record item and subsequently duplicate the record
 *
 * @author Mikedeejay2
 */
@Transformer("1.20-1.20.6")
public class TransformJukeboxEntitySetTheItem extends MappedMethodVisitor {
    private final int stackIdx = MinecraftVersion.check(">=1.20.4") ? 1 : 2;
    private boolean visitedIfNull = false;
    private boolean visitedLabel = false;

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
    public void visitJumpInsn(int opcode, Label label) {
        super.visitJumpInsn(opcode, label);
        if(!visitedIfNull && opcode == IFNULL) {
            this.visitedIfNull = true;
        }
    }

    @Override
    public void visitLabel(Label label) {
        super.visitLabel(label);
        if(visitedIfNull && !visitedLabel) {
            visitedLabel = true;
            appendFixStackSize();
        }
    }

    private void appendFixStackSize() {
        super.visitVarInsn(ALOAD, stackIdx); // Load the ItemStack
        super.visitInsn(ICONST_1);
        super.visitMethodInsn(INVOKEVIRTUAL, nms("ItemStack").method("setCount"));
    }
}
