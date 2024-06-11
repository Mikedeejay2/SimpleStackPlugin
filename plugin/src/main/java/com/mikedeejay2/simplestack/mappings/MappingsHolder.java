package com.mikedeejay2.simplestack.mappings;

import org.apache.commons.lang3.Validate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class MappingsHolder {
    private final Map<String, ClassMapping> mappings;

    MappingsHolder() {
        this.mappings = new HashMap<>();
    }

    void add(String name, ClassMapping mappings) {
        this.mappings.put(name, mappings);
    }

    boolean contains(String key) {
        return mappings.containsKey(key);
    }

    Collection<ClassMapping> get() {
        return mappings.values();
    }

    public ClassMapping clazz(String name) {
        Validate.isTrue(mappings.containsKey(name),
                        String.format("Tried to get invalid class mapping \"%s\"", name));
        return MappingsLookup.setLast(mappings.get(name));
    }
}
