package com.mikedeejay2.simplestack.bytecode;

import com.mikedeejay2.simplestack.mappings.MappingEntry;
import net.bytebuddy.asm.AsmVisitorWrapper;

public interface MethodVisitorInfo {
    AsmVisitorWrapper.ForDeclaredMethods.MethodVisitorWrapper getWrapper();
    MappingEntry getMappingEntry();

    default String[] getValidationMarkers() {
        return null;
    }
}
