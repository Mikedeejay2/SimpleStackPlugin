package com.mikedeejay2.simplestack.util;

import com.google.common.collect.Multimap;

import java.util.LinkedHashSet;
import java.util.Set;

public final class MultimapClassCollector {
    private MultimapClassCollector() {
        throw new UnsupportedOperationException("MultimapClassCollector cannot be instantiated as an object");
    }

    public static <C, T> Set<T> collect(Multimap<Class<? extends C>, T> multimap, Class<?> clazz) {
        final Set<T> set = new LinkedHashSet<>();
        collectRecursive(multimap, clazz, set);
        return set;
    }

    private static <C, T> void collectRecursive(Multimap<Class<? extends C>, T> multimap, Class<?> clazz, Set<T> set) {
        if(clazz.getSuperclass() != null) collectRecursive(multimap, clazz.getSuperclass(), set);
        if(multimap.containsKey(clazz)) set.addAll(multimap.get((Class<? extends C>) clazz)); // Will always extend C
        for(Class<?> interfaceClass : clazz.getInterfaces()) {
            collectRecursive(multimap, interfaceClass, set);
        }
    }
}
