package com.mikedeejay2.simplestack.bytecode;

import net.bytebuddy.asm.AsmVisitorWrapper;

public interface MethodVisitorInfo {
    AsmVisitorWrapper.ForDeclaredMethods.MethodVisitorWrapper getWrapper();
    MappingsLookup.MappingEntry getMappingEntry();
}
