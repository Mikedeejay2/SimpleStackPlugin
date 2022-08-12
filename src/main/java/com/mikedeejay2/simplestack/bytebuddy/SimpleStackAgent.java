package com.mikedeejay2.simplestack.bytebuddy;

import net.bytebuddy.NamingStrategy;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.ResettableClassFileTransformer;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.jar.asm.ClassVisitor;
import net.bytebuddy.jar.asm.ClassWriter;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.pool.TypePool;
import net.bytebuddy.utility.CompoundList;
import net.bytebuddy.utility.JavaModule;
import org.apache.commons.lang3.Validate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static net.bytebuddy.matcher.ElementMatchers.*;
import static net.bytebuddy.matcher.ElementMatchers.nameStartsWith;

public final class SimpleStackAgent {
    private static ResettableClassFileTransformer transformer;
    private static final Map<String, Set<MethodVisitorInfo>> VISITORS = new HashMap<>();

    public static void install() {
        ElementMatcher.Junction<? super TypeDescription> typeMatcher = none();
        for(String className : VISITORS.keySet()) {
            typeMatcher = typeMatcher.or(named(className));
        }

        transformer = new AgentBuilder.Default()
            .disableClassFormatChanges()
            .ignore(not(nameStartsWith("net.minecraft").or(nameStartsWith("org.bukkit"))))
            .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION) // Use retransformation strategy to modify existing NMS classes
            .type(typeMatcher) // Match only classes to be transformed
            .transform(MasterTransformer.INSTANCE) // Transform using MasterTransformer
            .installOn(ByteBuddyHolder.getInstrumentation()); // Inject
    }

    public static void reset() {
        ByteBuddyHolder.resetTransformer(transformer);
    }

    public static void addVisitor(MethodVisitorInfo visitor) {
        String className = visitor.getMappingEntry().owner().qualifiedName();
        VISITORS.putIfAbsent(className, new HashSet<>());
        VISITORS.get(className).add(visitor);
    }

    private enum MasterTransformer implements AgentBuilder.Transformer {
        INSTANCE;

        @Override
        public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module) {
            try {
//                AsmVisitorWrapper.ForDeclaredMethods wrapper = new AsmVisitorWrapper.ForDeclaredMethods();
                String className = typeDescription.getName();
                Set<MethodVisitorInfo> wrappers = VISITORS.get(className);
                Validate.notNull(wrappers,
                                 "No method visitors of name \"%s\" were found.", className);
                Validate.notEmpty(wrappers,
                                  "Got empty set of method visitors for name \"%s\"", className);
//                for(MethodVisitorInfo current : wrappers) {
//                    wrapper = wrapper.method(current.getMatcher(), current.getWrapper());
//                }
//                wrapper = wrapper.writerFlags(ClassWriter.COMPUTE_MAXS);
//
//                return builder.visit(wrapper);

                return builder.visit(new AgentAsmVisitor(wrappers));
            } catch(Exception e) {
                e.printStackTrace();
            }
            return builder;
        }
    }

    private static final class AgentAsmVisitor implements AsmVisitorWrapper {
        private final Set<MethodVisitorInfo> visitorInfos;

        public AgentAsmVisitor(Set<MethodVisitorInfo> visitorInfos) {
            this.visitorInfos = visitorInfos;
        }

        @Override
        public int mergeWriter(int flags) {
            return flags | ClassWriter.COMPUTE_MAXS;
        }

        @Override
        public int mergeReader(int flags) {
            return flags;
        }

        @Override
        public ClassVisitor wrap(
            TypeDescription instrumentedType,
            ClassVisitor classVisitor,
            Implementation.Context implementationContext,
            TypePool typePool,
            FieldList<FieldDescription.InDefinedShape> fields,
            MethodList<?> methods,
            int writerFlags,
            int readerFlags) {
            // Referenced from ASMVisitorWrapper line 491
            Map<String, MethodDescription> mapped = new HashMap<>();
            for (MethodDescription methodDescription : CompoundList.of(
                instrumentedType.getDeclaredMethods(), // Get all methods, including synthetic
                new MethodDescription.Latent.TypeInitializer(instrumentedType))) {
                mapped.put(methodDescription.getInternalName() + methodDescription.getDescriptor(), methodDescription);
            }
            return new AgentClassVisitor(
                classVisitor, visitorInfos, mapped, instrumentedType,
                implementationContext, typePool, writerFlags, readerFlags);
        }
    }

    private static final class AgentClassVisitor extends ClassVisitor {
        private final Set<MethodVisitorInfo> visitorInfos;
        private final Map<String, MethodDescription> methods;

        private final TypeDescription instrumentedType;
        private final Implementation.Context implementationContext;
        private final TypePool typePool;
        private final int writerFlags;
        private final int readerFlags;

        private AgentClassVisitor(
            ClassVisitor classVisitor,
            Set<MethodVisitorInfo> visitorInfos,
            Map<String, MethodDescription> methods,
            TypeDescription instrumentedType,
            Implementation.Context implementationContext,
            TypePool typePool,
            int writerFlags,
            int readerFlags) {
            super(Opcodes.ASM9, classVisitor);
            this.visitorInfos = visitorInfos;
            this.methods = methods;
            this.instrumentedType = instrumentedType;
            this.implementationContext = implementationContext;
            this.typePool = typePool;
            this.writerFlags = writerFlags;
            this.readerFlags = readerFlags;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            MethodVisitor visitor = super.visitMethod(access, name, descriptor, signature, exceptions);
            MethodDescription description = methods.get(name + descriptor);
            for(MethodVisitorInfo info : visitorInfos) {
                if(info.getMatcher().matches(description)) {
                    visitor = info.getWrapper().wrap(
                        instrumentedType, description, visitor,
                        implementationContext, typePool, writerFlags, readerFlags);
                }
            }
            return visitor;
        }
    }
}
