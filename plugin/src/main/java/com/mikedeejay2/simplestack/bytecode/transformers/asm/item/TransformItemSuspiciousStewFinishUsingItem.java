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
}
