package com.mikedeejay2.simplestack.asm;

import com.mikedeejay2.simplestack.asm.test.ProfileClassVisitor;
import com.mikedeejay2.simplestack.asm.test.TestProfile;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.lang.instrument.*;
import java.security.ProtectionDomain;

/**
 * @see <a href=https://bukkit.org/threads/tutorial-extreme-beyond-reflection-asm-replacing-loaded-classes.99376/>https://bukkit.org/threads/tutorial-extreme-beyond-reflection-asm-replacing-loaded-classes.99376/</a>
 */
public class SimpleStackAgent implements ClassFileTransformer {
    private static Instrumentation instrumentation = null;
    private static SimpleStackAgent transformer;

    public static void agentmain(String s, Instrumentation i) {
        System.out.println("Agent loaded!");

        // Initialization code
        transformer = new SimpleStackAgent();
        instrumentation = i;
        instrumentation.addTransformer(transformer);

        // To instrument, first revert all added bytecode
        // Call retransformClasses() on all modifiable methods
        try {
            instrumentation.redefineClasses(new ClassDefinition(TestProfile.class, ByteUtils.getBytesFromClass(TestProfile.class)));
        } catch(UnmodifiableClassException | ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("Failed to redefine class!");
        }
    }

    @Override
    public byte[] transform(
        ClassLoader loader, String className, Class<?> classBeingRedefined,
        ProtectionDomain protectionDomain, byte[] classFileBuffer)
        throws IllegalClassFormatException {
        if(loader != ClassLoader.getSystemClassLoader()) {
            System.err.printf("%s is not using the system loader, and so cannot be loaded!%n", className);
            return classFileBuffer;
        }
        if(className.startsWith("com/mikedeejay2/simplestack/asm/LateBindAttacher")) {
            System.err.printf("%s is part of profiling classes.%n", className);
            return classFileBuffer;
        }

        byte[] result = classFileBuffer;

        try {
            // Create class reader from buffer
            ClassReader reader = new ClassReader(classFileBuffer);
            // Make writer
            ClassWriter writer = new ClassWriter(0); // true
            ClassVisitor profiler = new ProfileClassVisitor(writer, className);
            // Add the class adapter as a modifier
            reader.accept(profiler, 0); //true
            result = writer.toByteArray();
            System.out.println("Returning reinstrumented class " + className);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
