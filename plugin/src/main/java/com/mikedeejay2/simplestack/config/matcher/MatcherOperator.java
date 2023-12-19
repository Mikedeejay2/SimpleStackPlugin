package com.mikedeejay2.simplestack.config.matcher;

import com.mikedeejay2.simplestack.api.MatcherOperatorType;

public final class MatcherOperator<T> {
    private final MatcherFunction<T> operator;
    private final String nameKey;
    private final Class<T> inputClass;
    private final MatcherOperatorType operatorType;

    public MatcherOperator(MatcherOperatorType operatorType, Class<T> inputClass, String nameKey, MatcherFunction<T> operator) {
        this.operator = operator;
        this.inputClass = inputClass;
        this.nameKey = "simplestack.config.matcher_operator." + nameKey;
        this.operatorType = operatorType;
        MatcherOperatorRegistry.REGISTRY_BY_CLASS.put(inputClass, this);
        MatcherOperatorRegistry.REGISTRY_BY_TYPE.put(operatorType, this);
    }

    public MatcherOperator(MatcherOperatorType operatorType, Class<T> inputClass, MatcherFunction<T> operator) {
        this(operatorType, inputClass, operatorType.name().toLowerCase(), operator);
    }

    public boolean match(T o1, T o2) {
        return operator.match(o1, o2);
    }

    public String getNameKey() {
        return nameKey;
    }

    public Class<T> getInputClass() {
        return inputClass;
    }

    public MatcherOperatorType getOperatorType() {
        return operatorType;
    }

    @FunctionalInterface
    public interface MatcherFunction<T> {
        boolean match(T o1, T o2);
    }
}
