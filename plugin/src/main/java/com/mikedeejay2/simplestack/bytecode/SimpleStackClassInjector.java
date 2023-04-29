package com.mikedeejay2.simplestack.bytecode;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.loading.ClassInjector;
import org.bukkit.Bukkit;

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
            TypeDescription.ForLoadedType::new,
            ClassFileLocator.ForClassLoader::read));
    }

    public Map<TypeDescription, Class<?>> inject() throws IOException {
        return getClassInjector().inject(getTypes());
    }

    private ClassInjector getClassInjector() {
        return new ClassInjector.UsingUnsafe(classLoader);
    }

    public static SimpleStackClassInjector of(ClassLoader classLoader, Class<?>... classes) {
        final SimpleStackClassInjector injector = new SimpleStackClassInjector(classLoader);
        for(Class<?> clazz : classes) {
            injector.addClass(clazz);
        }
        return injector;
    }
}
