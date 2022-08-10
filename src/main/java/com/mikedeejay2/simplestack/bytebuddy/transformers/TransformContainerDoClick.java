package com.mikedeejay2.simplestack.bytebuddy.transformers;

import com.mikedeejay2.simplestack.bytebuddy.SimpleStackMethodVisitor;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.jar.asm.Label;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.matcher.ElementMatcher;

import static com.mikedeejay2.simplestack.MappingsLookup.*;
import static net.bytebuddy.jar.asm.Opcodes.*;
import static net.bytebuddy.matcher.ElementMatchers.*;

public final class TransformContainerDoClick extends SimpleStackMethodVisitor {
    private boolean visitedIsSameItemSameTags = false;
    private boolean appendedStackCheck1 = false;
    private boolean appendedStackCheck2 = false;

    @Override
    public ElementMatcher.Junction<? super MethodDescription> getMatcher() {
        return named(getMappingEntry().name())
            .and(takesArgument(0, int.class))
            .and(takesArgument(1, int.class))
            .and(takesArgument(2, named(nms("InventoryClickType").qualifiedName())))
            .and(takesArgument(3, named(nms("EntityHuman").qualifiedName())));
    }

    @Override
    public MappingEntry getMappingEntry() {
        return nms("Container").method("doClick");
    }

    @Override
    public void visitCode() {
        super.visitCode();
        // Uncomment for debug message on visit code
        super.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        super.visitLdcInsn("Test of doClick method");
        super.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        if(!visitedIsSameItemSameTags && opcode == INVOKESTATIC &&
            owner.equals(nms("ItemStack").internalName()) &&
            name.equals(lastNms().method("isSameItemSameTags").name()) &&
            descriptor.equals(lastNmsMethod().descriptor())) {
            this.visitedIsSameItemSameTags = true;
        }
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        super.visitJumpInsn(opcode, label);
        if(!appendedStackCheck1 && visitedIsSameItemSameTags && opcode == Opcodes.IF_ICMPGT) { // Get the if statement "<=" to target second cursor swap action
            appendStackSizeCheck(label); // Add additional stack size check
            this.appendedStackCheck1 = true;
        } else if(!appendedStackCheck2 && visitedIsSameItemSameTags && opcode == IFEQ) { // Get the if statement to target first cursor swap action
            appendStackSizeCheck(label); // Add additional stack size check
            this.appendedStackCheck2 = true;
        }
    }

    /**
     * Fixes swapping cursor items with items of different types. Add an if statement to ensure that items being swapped
     * aren't overstacked.
     *
     * @param label The label to jump to if false
     */
    public void appendStackSizeCheck(Label label) {
        super.visitVarInsn(ALOAD, 7); // Get slot's ItemStack
        super.visitMethodInsn(INVOKEVIRTUAL, nms("ItemStack").method("getCount")); // Get ItemStack count
        super.visitVarInsn(ALOAD, 7); // Get slot's ItemStack
        super.visitMethodInsn(INVOKEVIRTUAL, nms("ItemStack").method("getMaxStackSize")); // Get ItemStack max stack size
        super.visitJumpInsn(IF_ICMPGT, label); // If count is less than or equal to getMaxStackSize, continue
    }
}
