package com.mikedeejay2.simplestack.config.matcher;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mikedeejay2.simplestack.api.MatcherOperatorType;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public final class MatcherOperatorRegistry {
    static final Multimap<Class<?>, MatcherOperator<?>> REGISTRY_BY_CLASS = HashMultimap.create();
    static final Map<MatcherOperatorType, MatcherOperator<?>> REGISTRY_BY_TYPE = new EnumMap<>(MatcherOperatorType.class);

    public static final MatcherOperator<Object> EQUALS          = new MatcherOperator<>(MatcherOperatorType.EQUALS, Object.class, "equals", Objects::equals);
    public static final MatcherOperator<String> STRING_CONTAINS = new MatcherOperator<>(MatcherOperatorType.STRING_CONTAINS, String.class, "contains", (s1, s2) -> s1 != null && s2 != null && s1.contains(s2));

    public static MatcherOperator<?> get(MatcherOperatorType type) {
        return REGISTRY_BY_TYPE.get(type);
    }

    private MatcherOperatorRegistry() {
        throw new UnsupportedOperationException("MatcherDataSourceRegistry cannot be instantiated as an object");
    }
}
