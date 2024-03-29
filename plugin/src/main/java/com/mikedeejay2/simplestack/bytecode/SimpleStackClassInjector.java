package com.mikedeejay2.simplestack.bytecode;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.loading.ClassInjector;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public final class SimpleStackClassInjector {
    private final Set<Class<?>> classes;
    private final ClassLoader classLoader;

    public SimpleStackClassInjector(ClassLoader classLoader) {
        this.classes = new HashSet<>();
        this.classLoader = classLoader;
    }

    public void addClass(Class<?> clazz) {
        classes.add(clazz);
    }

    private Map<TypeDescription, byte[]> getTypes() {
        return classes.stream().collect(Collectors.toMap(
            SimpleStackClassInjector::forLoadedType,
            SimpleStackClassInjector::forClassLoader));
    }

    private static TypeDescription.ForLoadedType forLoadedType(Class<?> clazz) {
        return new TypeDescription.ForLoadedType(clazz);
    }

    private static byte[] forClassLoader(Class<?> clazz) {
        try(ClassFileLocator locator = ClassFileLocator.ForClassLoader.of(SimpleStackClassInjector.class.getClassLoader())) {
            return locator.locate(clazz.getName()).resolve();
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<TypeDescription, Class<?>> inject() throws IOException {
        return getClassInjector().inject(getTypes());
    }

    private ClassInjector getClassInjector() {
        return new ClassInjector.UsingReflection(classLoader);
    }

    public static SimpleStackClassInjector of(ClassLoader classLoader, Class<?>... classes) {
        final SimpleStackClassInjector injector = new SimpleStackClassInjector(classLoader);
        for(Class<?> clazz : classes) {
            injector.addClass(clazz);
        }
        return injector;
    }
}
