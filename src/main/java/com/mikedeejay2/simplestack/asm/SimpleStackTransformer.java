package com.mikedeejay2.simplestack.asm;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

public class SimpleStackTransformer implements ClassFileTransformer {
    @Override
    public byte[] transform(
        ClassLoader loader, String className, Class<?> classBeingRedefined,
        ProtectionDomain protectionDomain, byte[] classFileBuffer) {
        return classFileBuffer;
        // Example class
    }
}
