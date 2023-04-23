package com.mikedeejay2.simplestack.bytecode;

import com.mikedeejay2.mikedeejay2lib.reflect.*;
import com.mikedeejay2.mikedeejay2lib.util.debug.CrashReportSection;
import com.mikedeejay2.mikedeejay2lib.util.structure.tuple.MutablePair;
import com.mikedeejay2.mikedeejay2lib.util.structure.tuple.Pair;
import com.mikedeejay2.mikedeejay2lib.util.version.MinecraftVersion;
import com.mikedeejay2.simplestack.SimpleStack;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.ResettableClassFileTransformer;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.Implementation;
import org.bukkit.Bukkit;
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
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static net.bytebuddy.matcher.ElementMatchers.*;

public final class SimpleStackAgent {
    private static ResettableClassFileTransformer transformer;
    private static final Map<String, Set<Pair<MethodVisitorInfo, Boolean>>> VISITORS = new HashMap<>();
    private static final AtomicBoolean crashed = new AtomicBoolean(false);
    private static MethodVisitorInfo lastVisitedInfo = null;

    public static boolean registerTransformers() {
        try {
            final String mcVersion = MinecraftVersion.getVersionString();
            new AnnotationCollector<>(
                new ClassCollector<>(
                    SimpleStack.getInstance().classLoader(), // Use the plugin class loader
                    SimpleStackAgent.class.getPackage().getName() + ".transformers", // Traverse in the transformers package
                    true, // Traverse sub-packages
                    MethodVisitorInfo.class), // Only locate classes that are subclasses of MethodVisitorInfo
                Transformer.class) // Locate the Transformer annotation
                .collect()
                .stream()
                .filter(pair -> Arrays.asList(pair.getValue().value()).contains(mcVersion)) // If current Minecraft version not found, don't use the transformer
                .map(pair -> Reflector.of(pair.getKey()).constructor().newInstance()) // Create a new instance (no arg) of the transformer
                .forEach(SimpleStackAgent::addVisitor); // Add the new visitor
            return false;
        } catch(Throwable throwable) {
            SimpleStack.doCrash("Exception while collecting transformers", throwable, c -> {});
            return true;
        }
    }

    private static void addVisitor(MethodVisitorInfo visitor) {
        String className = visitor.getMappingEntry().owner().qualifiedName();
        VISITORS.putIfAbsent(className, new HashSet<>());
        VISITORS.get(className).add(new MutablePair<>(visitor, false));
    }

    public static boolean install() {
        Validate.isTrue(VISITORS.size() != 0, "No transformers found for installation");
        final ElementMatcher.Junction<? super TypeDescription> typeMatcher = generateTypeMatcher();
        if(typeMatcher == null) return true;

        // Move AdviceBridge to Minecraft's ClassLoader so advice can access specific SimpleStack methods
        if(injectAdviceBridge()) return true;

        // Install transformer
        transformer = new AgentBuilder.Default()
            .disableClassFormatChanges()
            .ignore(not(nameStartsWith("net.minecraft").or(nameStartsWith("org.bukkit"))))
            .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION) // Use retransformation strategy to modify existing NMS classes
            .type(typeMatcher) // Match only classes to be transformed
            .transform(MasterTransformer.INSTANCE) // Transform using MasterTransformer
            .with(ExceptionListener.INSTANCE)
            .with(InstallationExceptionListener.INSTANCE)
            .installOn(ByteBuddyHolder.getInstrumentation()); // Inject

        return crashed.get() || detectNotVisited();
    }

    private static boolean injectAdviceBridge() {
        try {
            final Class<?> adviceBridgeClass = injectClass(AdviceBridge.class);
            adviceBridgeClass.getMethod("initialize").invoke(null);
        } catch(Throwable throwable) {
            SimpleStack.doCrash("Exception while injecting Advice bridge", throwable, c -> {});
            return true;
        }
        return false;
    }

    private static Class<?> injectClass(Class<?> clazz) throws ClassNotFoundException {
        final ClassLoader classLoader = Bukkit.class.getClassLoader();
        // Takes class from the plugin's ClassLoader and loads it into Minecraft's ClassLoader
        final ClassFileLocator classFileLocator = ClassFileLocator.ForClassLoader.of(SimpleStack.getInstance().classLoader());
        final ClassReloadingStrategy classReloadingStrategy = ClassReloadingStrategy.fromInstalledAgent(ClassReloadingStrategy.Strategy.RETRANSFORMATION);
        Class<?> adviceBridgeClass = new ByteBuddy()
            .redefine(clazz, classFileLocator)
            .make()
            .load(classLoader, classReloadingStrategy)
            .getLoaded();
        classLoader.loadClass(adviceBridgeClass.getName());
        return adviceBridgeClass;
    }

    private static ElementMatcher.Junction<? super TypeDescription> generateTypeMatcher() {
        ElementMatcher.Junction<? super TypeDescription> typeMatcher = none();
        final ClassLoader classLoader = Bukkit.class.getClassLoader();
        for(String className : VISITORS.keySet()) {
            try { // Load the class, this fixes some classes not being loaded during scans
                classLoader.loadClass(className);
            } catch(ClassNotFoundException e) {
                SimpleStack.doCrash("Exception while loading class during type matcher creation", e, crashReport -> {
                    CrashReportSection section = crashReport.addSection("Class Details");
                    section.addDetail("Class Name", className);
                });
                return null;
            }
            typeMatcher = typeMatcher.or(named(className));
        }
        return typeMatcher;
    }

    private static boolean detectNotVisited() {
        final Map<String, Set<Pair<MethodVisitorInfo, Boolean>>> notVisited = new HashMap<>();

        for(String key : VISITORS.keySet()) {
            final Set<Pair<MethodVisitorInfo, Boolean>> set = VISITORS.get(key);
            final Set<Pair<MethodVisitorInfo, Boolean>> curSet = new LinkedHashSet<>();
            for(Pair<MethodVisitorInfo, Boolean> pair : set) {
                if(pair.getValue()) continue;
                curSet.add(pair);
            }
            if(curSet.isEmpty()) continue;
            notVisited.put(key, curSet);
        }

        if(notVisited.isEmpty()) return false;
        SimpleStack.doCrash("Registered transformer not visited after transformations", null, crashReport -> {
            CrashReportSection section = crashReport.addSection("Unvisited Transformers");
            section.addDetail("Transformers", getTransformersString(notVisited));
        });
        return true;
    }

    public static void reset() {
        ByteBuddyHolder.resetTransformer(transformer);
    }

    public static void fillCrashReportSection(CrashReportSection section) {
        section.addDetail("Last Visited Transformer", lastVisitedInfo != null ? lastVisitedInfo.getClass().getSimpleName() : "null");
        section.addDetail("Transformers", getTransformersString(VISITORS));
    }

    private static String getTransformersString(Map<String, Set<Pair<MethodVisitorInfo, Boolean>>> visitors) {
        StringBuilder builder = new StringBuilder();
        for(String className : visitors.keySet()) {
            builder.append("\n    ").append(className).append(":");
            for(Pair<MethodVisitorInfo, Boolean> pair : visitors.get(className)) {
                final MethodVisitorInfo info = pair.getLeft();
                builder.append("\n      ")
                    .append(info.getClass().getSimpleName())
                    .append(" [")
                    .append(info.getMappingEntry().owner().qualifiedName())
                    .append(".")
                    .append(info.getMappingEntry().name())
                    .append(info.getMappingEntry().descriptor())
                    .append("], transformed: ")
                    .append(pair.getRight());
            }
        }
        return builder.toString();
    }

    private enum MasterTransformer implements AgentBuilder.Transformer {
        INSTANCE;

        @Override
        public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, ProtectionDomain protectionDomain) {
            if(crashed.get()) return builder;
            String className = typeDescription.getName();
            Set<Pair<MethodVisitorInfo, Boolean>> wrappers = VISITORS.get(className);
            Validate.notNull(wrappers,
                             "No method visitors of name \"%s\" were found.", className);
            Validate.notEmpty(wrappers,
                              "Got empty set of method visitors for name \"%s\"", className);
            return builder.visit(new AgentAsmVisitor(wrappers));
        }
    }

    private static final class AgentAsmVisitor implements AsmVisitorWrapper {
        private final Set<Pair<MethodVisitorInfo, Boolean>> visitorInfos;

        public AgentAsmVisitor(Set<Pair<MethodVisitorInfo, Boolean>> visitorInfos) {
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
            if(crashed.get()) return classVisitor;
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
        private final Set<Pair<MethodVisitorInfo, Boolean>> visitorInfos;
        private final Map<String, MethodDescription> methods;

        private final TypeDescription instrumentedType;
        private final Implementation.Context implementationContext;
        private final TypePool typePool;
        private final int writerFlags;
        private final int readerFlags;

        private AgentClassVisitor(
            ClassVisitor classVisitor,
            Set<Pair<MethodVisitorInfo, Boolean>> visitorInfos,
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
            if(crashed.get()) return visitor;
            MethodDescription description = methods.get(name + descriptor);
            for(Pair<MethodVisitorInfo, Boolean> pair : visitorInfos) {
                final MethodVisitorInfo info = pair.getLeft();
                if(!info.getMappingEntry().matches(name, descriptor)) continue;
                // Uncomment to print out current MethodVisitorInfo
                // System.out.println(info.getMappingEntry().owner().internalName() + "." + info.getMappingEntry().name() + info.getMappingEntry().descriptor());
                lastVisitedInfo = info;
                visitor = info.getWrapper().wrap(
                    instrumentedType, description, visitor,
                    implementationContext, typePool, writerFlags, readerFlags);
                pair.setValue(true);
            }
            return visitor;
        }
    }

    private enum ExceptionListener implements AgentBuilder.Listener {
        INSTANCE;

        @Override
        public void onError(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded, Throwable throwable) {
            SimpleStack.doCrash("Exception while transforming classes", throwable, crashReport -> {
                CrashReportSection section = crashReport.addSection("Transform Details");
                section.addDetail("Type Name", typeName);
                section.addDetail("Loaded", String.valueOf(loaded));
            });
            crashed.compareAndSet(false, true);
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
            SimpleStack.doCrash("Exception while transforming classes", throwable, crashReport -> {
                CrashReportSection section = crashReport.addSection("Install Details");
                section.addDetail("Class File Transformer", classFileTransformer.getClass().getCanonicalName());
            });
            crashed.compareAndSet(false, true);
            return null;
        }

        @Override public void onBeforeInstall(Instrumentation instrumentation, ResettableClassFileTransformer classFileTransformer) {}
        @Override public void onInstall(Instrumentation instrumentation, ResettableClassFileTransformer classFileTransformer) {}
        @Override public void onReset(Instrumentation instrumentation, ResettableClassFileTransformer classFileTransformer) {}
        @Override public void onBeforeWarmUp(Set<Class<?>> types, ResettableClassFileTransformer classFileTransformer) {}
        @Override public void onWarmUpError(Class<?> type, ResettableClassFileTransformer classFileTransformer, Throwable throwable) {}
        @Override public void onAfterWarmUp(Map<Class<?>, byte[]> types, ResettableClassFileTransformer classFileTransformer, boolean transformed) {}
    }
}
