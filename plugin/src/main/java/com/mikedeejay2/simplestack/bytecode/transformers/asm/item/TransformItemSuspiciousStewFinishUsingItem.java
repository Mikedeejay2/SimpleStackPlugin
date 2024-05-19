package com.mikedeejay2.simplestack.bytecode.transformers.asm.item;

import com.mikedeejay2.mikedeejay2lib.util.version.MinecraftVersion;
import com.mikedeejay2.simplestack.bytecode.MappingsLookup;
import com.mikedeejay2.simplestack.bytecode.Transformer;

import static com.mikedeejay2.simplestack.bytecode.MappingsLookup.*;
import static org.objectweb.asm.Opcodes.*;

/**
 * Fixes stacked suspicious stews from being replaced by a bowl upon use.
 *
 * @author Mikedeejay2
 */
@Transformer("1.18-1.20.6")
public class TransformItemSuspiciousStewFinishUsingItem extends TransformItemSoupFinishUsingItem {
    @Override
    public MappingsLookup.MappingEntry getMappingEntry() {
        return nms("ItemSuspiciousStew").method("finishUsingItem");
    }

    @Override
    public void visitCode() {
        // 1.20.6 changes suspicious stew item stack var index
        if(MinecraftVersion.check(">=1.20.6")) {
            super.stackIndex = 1;
        }
        super.visitCode();
    }

    @Override
    public void visitFrame(int type, int numLocal, Object[] local, int numStack, Object[] stack) {
        if(!visitedFrame && visitedAload && MinecraftVersion.check("<=1.19.3,>=1.20.6")) { // Target the frame after the first return statement
            super.visitedFrame = true;
            System.out.println("Frame THIS for " + this.getClass().getSimpleName());
            // Instead of F_APPEND, F_SAME is instead used for suspicious stew.
            super.visitFrame(F_SAME, 0, null, 0, null);
            return;
        }
        super.visitFrame(type, numLocal, local, numStack, stack);
    }
}
