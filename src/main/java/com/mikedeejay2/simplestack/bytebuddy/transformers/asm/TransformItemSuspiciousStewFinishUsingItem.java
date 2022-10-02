package com.mikedeejay2.simplestack.bytebuddy.transformers.asm;

import com.mikedeejay2.simplestack.MappingsLookup;

import static com.mikedeejay2.simplestack.MappingsLookup.*;
import static org.objectweb.asm.Opcodes.*;

/**
 * Fixes stacked suspicious stews from being replaced by a bowl upon use.
 *
 * @author Mikedeejay2
 */
public class TransformItemSuspiciousStewFinishUsingItem extends TransformItemSoupFinishUsingItem {
    @Override
    public MappingsLookup.MappingEntry getMappingEntry() {
        return nms("ItemSuspiciousStew").method("finishUsingItem");
    }

    @Override
    public void visitFrame(int type, int numLocal, Object[] local, int numStack, Object[] stack) {
        if(!visitedFrame && visitedReturn) { // Target the frame after the first return statement
            visitedFrame = true;
            // Instead of F_APPEND, F_SAME is instead used for suspicious stew.
            super.visitFrame(F_SAME, 0, null, 0, null);
            return;
        }
        super.visitFrame(type, numLocal, local, numStack, stack);
    }
}
