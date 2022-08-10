package com.mikedeejay2.simplestack.bytebuddy;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.ResettableClassFileTransformer;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.jar.asm.ClassWriter;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.utility.JavaModule;
import org.apache.commons.lang3.Validate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static net.bytebuddy.matcher.ElementMatchers.*;

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
                AsmVisitorWrapper.ForDeclaredMethods wrapper = new AsmVisitorWrapper.ForDeclaredMethods();
                String className = typeDescription.getName();
                Set<MethodVisitorInfo> wrappers = VISITORS.get(className);
                Validate.notNull(wrappers,
                                 "No method visitors of name \"%s\" were found.", className);
                Validate.notEmpty(wrappers,
                                  "Got empty set of method visitors for name \"%s\"", className);
                for(MethodVisitorInfo current : wrappers) {
                    wrapper = wrapper.method(current.getMatcher(), current.getWrapper());
                }
                wrapper = wrapper.writerFlags(ClassWriter.COMPUTE_MAXS);

                return builder.visit(wrapper);
            } catch(Exception e) {
                e.printStackTrace();
            }
            return builder;
        }
    }
}
