package com.mikedeejay2.simplestack.bytebuddy.transformers;

import com.mikedeejay2.simplestack.MappingsLookup;
import com.mikedeejay2.simplestack.bytebuddy.SimpleStackMethodVisitor;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;

import java.util.Optional;

import static net.bytebuddy.jar.asm.Opcodes.*;
import static net.bytebuddy.matcher.ElementMatchers.*;
import static com.mikedeejay2.simplestack.MappingsLookup.*;

public final class TransformSlotTryRemove extends SimpleStackMethodVisitor {
    @Override
    public ElementMatcher.Junction<? super MethodDescription> getMatcher() {
        return named(getMappingEntry().name())
            .and(takesArgument(0, int.class))
            .and(takesArgument(1, int.class))
            .and(returns(Optional.class));
    }

    @Override
    public MappingsLookup.MappingEntry getMappingEntry() {
        return nms("Slot").method("tryRemove");
    }

    @Override
    public void visitCode() {
        super.visitCode();
        // Uncomment for debug message on visit code
//            super.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
//            super.visitLdcInsn("Test of tryRemove method");
//            super.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        if(opcode == INVOKESTATIC && owner.equals("java/lang/Math") && name.equals("min")) { // Target Math.min() invocation
            appendClampToMaxStackSize();
        }
    }

    private void appendClampToMaxStackSize() {
        super.visitVarInsn(ALOAD, 0); // Get this slot
        super.visitMethodInsn(
            INVOKEVIRTUAL, nms("Slot").method("getItem")); // Get the ItemStack currently in the slot
        super.visitMethodInsn(
            INVOKEVIRTUAL, nms("ItemStack").method("getMaxStackSize")); // Get the max stack size of the ItemStack in the slot
        super.visitMethodInsn(
            INVOKESTATIC, "java/lang/Math", "min",
            "(II)I", false); // Call Math.min()
        // Next instruction stores the result
    }
}
