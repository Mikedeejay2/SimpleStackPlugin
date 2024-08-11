package com.mikedeejay2.simplestack.bytecode.transformers.asm.item;

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
@Transformer("1.18-1.21.1")
public class TransformJukeboxEntitySetTheItem extends MappedMethodVisitor {
    private final int stackIdx = nms("JukeboxBlockEntity").method("setTheItem").descriptor().contains("(I") ? 2 : 1;

    @Override
    public MappingEntry getMappingEntry() {
        return nms("JukeboxBlockEntity").method("setTheItem");
    }

    @Override
    public void visitCode() {
        super.visitCode();
//        System.out.println("setTheItem");
        appendFixStackSize();
    }

    private void appendFixStackSize() {
        Label emptyLabel = new Label();
        Label afterLabel = new Label();

        super.visitVarInsn(ALOAD, stackIdx); // Load ItemStack (for later)
        super.visitInsn(DUP); // Load ItemStack again
        super.visitMethodInsn(INVOKEVIRTUAL, nms("ItemStack").method("getCount")); // Get the current count of the ItemStack
        super.visitInsn(ICONST_1); // Load int 1
        super.visitMethodInsn(
            INVOKESTATIC, "java/lang/Math", "min",
            "(II)I", false); // Call Math.min() with the min stack size and the current size
        super.visitInsn(DUP);
        super.visitInsn(ICONST_0);

        // In 1.19 versions, if setCount is called on ItemStack.EMPTY, it crashes the server
        super.visitJumpInsn(IF_ICMPEQ, emptyLabel); // If Math.min and 0 are equal, don't set count

        super.visitMethodInsn(INVOKEVIRTUAL, nms("ItemStack").method("setCount")); // Set the item to the minimum amount
        super.visitJumpInsn(GOTO, afterLabel);
        super.visitLabel(emptyLabel);

        super.visitInsn(POP2);

        super.visitLabel(afterLabel);
    }
}
