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
@Transformer({
    "1.20.2", "1.20.1", "1.20",
    "1.19", "1.19.1", "1.19.2", "1.19.3", "1.19.4",
    "1.18", "1.18.1", "1.18.2"
})
public class TransformItemSuspiciousStewFinishUsingItem extends TransformItemSoupFinishUsingItem {
    @Override
    public MappingsLookup.MappingEntry getMappingEntry() {
        return nms("ItemSuspiciousStew").method("finishUsingItem");
    }

    @Override
    public void visitFrame(int type, int numLocal, Object[] local, int numStack, Object[] stack) {
        boolean shouldSameFrame = !(MinecraftVersion.getVersionLong()[1] == 19 &&
            MinecraftVersion.getVersionLong()[2] >= 3 ||
            MinecraftVersion.getVersionLong()[1] >= 20);
        if(!visitedFrame && visitedAload && shouldSameFrame) { // Target the frame after the first return statement
            visitedFrame = true;
            // Instead of F_APPEND, F_SAME is instead used for suspicious stew.
            super.visitFrame(F_SAME, 0, null, 0, null);
            return;
        }
        super.visitFrame(type, numLocal, local, numStack, stack);
    }
}
