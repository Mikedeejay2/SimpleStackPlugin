package com.mikedeejay2.simplestack.bytebuddy;

import com.mikedeejay2.simplestack.MappingsLookup;
import net.bytebuddy.asm.AsmVisitorWrapper;

public interface MethodVisitorInfo {
    AsmVisitorWrapper.ForDeclaredMethods.MethodVisitorWrapper getWrapper();
    MappingsLookup.MappingEntry getMappingEntry();
}