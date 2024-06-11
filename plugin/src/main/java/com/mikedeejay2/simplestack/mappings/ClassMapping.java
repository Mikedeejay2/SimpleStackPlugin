package com.mikedeejay2.simplestack.mappings;

import org.apache.commons.lang3.Validate;

import java.util.*;

public final class ClassMapping {
    private final String referenceName;
    private String qualifiedName;
    private String internalName;
    private String descriptorName;
    private final Map<String, MappingEntry> methodMappings;
    private final Map<String, MappingEntry> fieldMappings;
    private final List<String> alternates;

    ClassMapping(String qualifiedName, String referenceName) {
        this.referenceName = referenceName;
        this.methodMappings = new HashMap<>();
        this.fieldMappings = new HashMap<>();
        this.alternates = new ArrayList<>();
        initFields(qualifiedName);
    }

    private void initFields(String qualifiedName) {
        this.qualifiedName = qualifiedName;
        this.internalName = qualifiedName.replace('.', '/');
        this.descriptorName = "L" + internalName + ";";
    }

    void method(String name, MappingEntry entry) {
        entry.owner(this);
        methodMappings.put(name, entry);
    }

    void removeMethod(String name) {
        methodMappings.remove(name);
    }

    void field(String name, MappingEntry entry) {
        entry.owner(this);
        fieldMappings.put(name, entry);
    }

    void removeField(String name) {
        fieldMappings.remove(name);
    }

    void setQualifiedName(String qualifiedName) {
        initFields(qualifiedName);
    }

    void alternate(String alternate) {
        this.alternates.add(alternate);
    }

    void alternates(List<String> alternate) {
        this.alternates.addAll(alternate);
    }

    void useAlternate(String alternate) {
        Validate.isTrue(alternates.contains(alternate), "Tried to use mapping that wasn't an alternate");
        initFields(alternate);
    }

    Collection<MappingEntry> methodEntries() {
        return methodMappings.values();
    }

    Collection<MappingEntry> fieldEntries() {
        return fieldMappings.values();
    }

    public String referenceName() {
        return referenceName;
    }

    public String qualifiedName() {
        return qualifiedName;
    }

    public String internalName() {
        return internalName;
    }

    public String descriptorName() {
        return descriptorName;
    }

    public List<String> alternates() {
        return alternates;
    }

    public MappingEntry method(String name) {
        Validate.isTrue(methodMappings.containsKey(name),
                        String.format("Tried to get invalid method mapping \"%s\"", name));
        return methodMappings.get(name);
    }

    public MappingEntry field(String name) {
        Validate.isTrue(fieldMappings.containsKey(name),
                        String.format("Tried to get invalid field mapping \"%s\"", name));
        return fieldMappings.get(name);
    }

    public Class<?> toClass() {
        try {
            return Class.forName(qualifiedName);
        } catch(ClassNotFoundException e) {
            throw new RuntimeException(String.format("Could not get class of name \"%s\"", qualifiedName), e);
        }
    }

    @Override
    public String toString() {
        return "ClassMapping{" +
            "qualifiedName='" + qualifiedName + '\'' +
            ", internalName='" + internalName + '\'' +
            ", descriptorName='" + descriptorName + '\'' +
            '}';
    }
}
