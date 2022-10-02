package com.mikedeejay2.simplestack.bytebuddy;

import com.mikedeejay2.mikedeejay2lib.util.debug.CrashReport;
import com.mikedeejay2.mikedeejay2lib.util.debug.CrashReportSection;
import com.mikedeejay2.mikedeejay2lib.util.debug.ReportedException;
import com.mikedeejay2.simplestack.SimpleStack;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.ResettableClassFileTransformer;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.scaffold.TypeWriter;
import net.bytebuddy.implementation.Implementation;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.pool.TypePool;
import net.bytebuddy.utility.CompoundList;
import net.bytebuddy.utility.JavaModule;
import org.apache.commons.lang3.Validate;
import org.objectweb.asm.util.CheckClassAdapter;

import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
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
            .ignore(not(nameStartsWith("net.minecraft").or(nameStartsWith("org.bukkit"))))
            .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION) // Use retransformation strategy to modify existing NMS classes
            .type(typeMatcher) // Match only classes to be transformed
            .transform(MasterTransformer.INSTANCE) // Transform using MasterTransformer
            .with(ExceptionListener.INSTANCE)
            .with(InstallationExceptionListener.INSTANCE)
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
        public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, ProtectionDomain protectionDomain) {
                String className = typeDescription.getName();
                Set<MethodVisitorInfo> wrappers = VISITORS.get(className);
                Validate.notNull(wrappers,
                                 "No method visitors of name \"%s\" were found.", className);
                Validate.notEmpty(wrappers,
                                  "Got empty set of method visitors for name \"%s\"", className);
                return builder.visit(new AgentAsmVisitor(wrappers));
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
            classVisitor = new CheckClassAdapter(classVisitor);
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
                if(!info.getMappingEntry().matches(name, descriptor)) continue;
                visitor = info.getWrapper().wrap(
                    instrumentedType, description, visitor,
                    implementationContext, typePool, writerFlags, readerFlags);
            }
            return visitor;
        }
    }

    private enum ExceptionListener implements AgentBuilder.Listener {
        INSTANCE;

        @Override
        public void onError(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded, Throwable throwable) {
            CrashReport crashReport = new CrashReport(SimpleStack.getInstance(), "Exception while transforming classes", true);
            crashReport.setThrowable(throwable);

            CrashReportSection section = crashReport.addSection("Transform Details");
            section.addDetail("Type Name", typeName);
            section.addDetail("Class Loader", classLoader != null ? classLoader.getName() : null);
            section.addDetail("Loaded", String.valueOf(loaded));

            crashReport.execute();
        }

        @Override public void onComplete(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {}
        @Override public void onDiscovery(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {}
        @Override public void onTransformation(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, boolean loaded, DynamicType dynamicType) {}
        @Override public void onIgnored(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, boolean loaded) {}
    }

    private enum InstallationExceptionListener implements AgentBuilder.InstallationListener {
        INSTANCE;

        @Override
        public Throwable onError(Instrumentation instrumentation, ResettableClassFileTransformer classFileTransformer, Throwable throwable) {
            CrashReport crashReport = new CrashReport(SimpleStack.getInstance(), "Exception while transforming classes", true);
            crashReport.setThrowable(throwable);

            CrashReportSection section = crashReport.addSection("Install Details");
            section.addDetail("Class File Transformer", classFileTransformer.getClass().getCanonicalName());

            crashReport.execute();
            return new ReportedException(crashReport);
        }

        @Override public void onBeforeInstall(Instrumentation instrumentation, ResettableClassFileTransformer classFileTransformer) {}
        @Override public void onInstall(Instrumentation instrumentation, ResettableClassFileTransformer classFileTransformer) {}
        @Override public void onReset(Instrumentation instrumentation, ResettableClassFileTransformer classFileTransformer) {}
        @Override public void onBeforeWarmUp(Set<Class<?>> types, ResettableClassFileTransformer classFileTransformer) {}
        @Override public void onWarmUpError(Class<?> type, ResettableClassFileTransformer classFileTransformer, Throwable throwable) {}
        @Override public void onAfterWarmUp(Map<Class<?>, byte[]> types, ResettableClassFileTransformer classFileTransformer, boolean transformed) {}
    }
}
