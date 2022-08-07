package com.mikedeejay2.simplestack.bytebuddy;

import com.mikedeejay2.simplestack.MappingsLookup;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;

public interface SimpleStackWrapper {
    AsmVisitorWrapper.ForDeclaredMethods.MethodVisitorWrapper getWrapper();
    ElementMatcher.Junction<? super MethodDescription> getMatcher();
    MappingsLookup.MappingEntry getMappingEntry();
}
