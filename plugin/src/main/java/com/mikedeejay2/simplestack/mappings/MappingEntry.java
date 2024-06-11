package com.mikedeejay2.simplestack.mappings;

import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.List;

import static com.mikedeejay2.simplestack.mappings.MappingsLookup.nms;

public final class MappingEntry {
    private final String referenceName;
    private String name;
    private ClassMapping owner;
    private String descriptorFormat;
    private String descriptor;
    private final List<String> alternates;

    MappingEntry(String value, String referenceName) {
        initFields(value);
        this.referenceName = referenceName;
        this.alternates = new ArrayList<>();
    }

    private void initFields(String value) {
        Validate.isTrue(value.contains(":") || value.contains("("),
                        "Mapping doesn't have descriptor, \"%s\"", value);
        value = value.replaceFirst("\\(", ":("); // Add a separator between method name and descriptor
        this.name = value.substring(0, value.indexOf(':'));
        this.descriptor(value.substring(value.indexOf(':') + 1));
    }

    void descriptor(String descriptor) {
        this.descriptorFormat = descriptor;
        this.descriptor = null;
    }

    void owner(ClassMapping owner) {
        this.owner = owner;
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

    public List<String> alternates() {
        return alternates;
    }

    public String referenceName() {
        return referenceName;
    }

    public String name() {
        return name;
    }

    public ClassMapping owner() {
        return owner;
    }

    public String descriptor() {
        Validate.notNull(descriptorFormat,
                         String.format("Tried to get invalid descriptor \"%s\"", referenceName));
        if(descriptor != null)
            return descriptor;
        return generateDescriptor();
    }

    private String generateDescriptor() {
        String newDescriptor = descriptorFormat;
        int index = newDescriptor.indexOf('L');
        while(index != -1) {
            int endIndex = newDescriptor.indexOf(';', index);
            Validate.isTrue(endIndex != -1); // No ending to the class reference
            final String placeholder = newDescriptor.substring(index + 1, endIndex);
            if(!placeholder.contains("/")) { // Not a qualified class name, find by nms mappings
                final ClassMapping classMapping = nms(placeholder);
                Validate.notNull(classMapping); // No result found for the placeholder
                final String qualifiedName = classMapping.internalName();
                newDescriptor = newDescriptor.substring(0, index + 1) + qualifiedName + newDescriptor.substring(endIndex);
            }
            endIndex = newDescriptor.indexOf(';', index); // Update end index
            Validate.isTrue(endIndex != -1); // No ending to the class reference
            index = newDescriptor.indexOf('L', endIndex); // Navigate to the start of the next class reference
        }
//            System.out.println("Descriptor: " + name + " " + newDescriptor);
        return (descriptor = newDescriptor);
    }

    public boolean matches(String name, String descriptor) {
        return name.equals(name()) && descriptor.equals(descriptor());
    }

    @Override
    public String toString() {
        return "MappingEntry{" +
            "name='" + name + '\'' +
            ", referenceName='" + referenceName + '\'' +
            ", descriptorFormat='" + descriptorFormat + '\'' +
            ", descriptor='" + descriptor + '\'' +
            '}';
    }
}
