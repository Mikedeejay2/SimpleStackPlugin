package com.mikedeejay2.simplestack.bytebuddy;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mikedeejay2.mikedeejay2lib.data.json.JsonAccessor;
import com.mikedeejay2.mikedeejay2lib.data.json.JsonFile;
import com.mikedeejay2.mikedeejay2lib.util.version.MinecraftVersion;
import com.mikedeejay2.simplestack.SimpleStack;
import org.apache.commons.lang3.Validate;

import java.util.HashMap;
import java.util.Map;

public class MappingsLookup {
    private static MappingsHolder holder = null;
    private static ClassMapping lastClass = null;
    private static MappingEntry lastMethod = null;
    private static MappingEntry lastField = null;

    public static boolean loadMappings(SimpleStack plugin) {
        final JsonFile jsonFile = new JsonFile(plugin, String.format("nms/%s.json", MinecraftVersion.getVersionString()));
        if(!jsonFile.loadFromJar(false)) return false; // File couldn't load, fail
        final JsonAccessor accessor = jsonFile.getAccessor();
        holder = new MappingsHolder();
        for(String classKey : accessor.getKeys(false)) {
            final JsonElement classElement = accessor.get(classKey);
            if(classElement.isJsonPrimitive()) { // Class entry holds no methods or fields, add it and continue
                holder.add(classKey, new ClassMapping(classElement.getAsString()));
                continue;
            }
            Validate.isTrue(classElement.isJsonObject()); // Should be an object
            final JsonObject classObject = classElement.getAsJsonObject();
            Validate.isTrue(classObject.has("class_name")); // Should have a class name
            final ClassMapping classMapping = new ClassMapping(classObject.get("class_name").getAsString());
            holder.add(classKey, classMapping);

            if(classObject.has("methods")) {
               final JsonElement methodsElement = classObject.get("methods");
               Validate.isTrue(methodsElement.isJsonObject()); // Should be an object
                final JsonObject methodsObject = methodsElement.getAsJsonObject();
                for(String methodKey : methodsObject.keySet()) {
                    final JsonElement methodElement = methodsObject.get(methodKey);
                    Validate.isTrue(methodElement.isJsonPrimitive()); // Should only be a String
                    classMapping.method(methodKey, new MappingEntry(methodElement.getAsString()));
                }
            }
            if(classObject.has("fields")) {
                final JsonElement fieldsElement = classObject.get("fields");
                Validate.isTrue(fieldsElement.isJsonObject()); // Should be an object
                final JsonObject fieldsObject = fieldsElement.getAsJsonObject();
                for(String fieldKey : fieldsObject.keySet()) {
                    final JsonElement fieldElement = fieldsObject.get(fieldKey);
                    Validate.isTrue(fieldElement.isJsonPrimitive()); // Should only be a String
                    classMapping.field(fieldKey, new MappingEntry(fieldElement.getAsString()));
                }
            }
        }

        // Generate descriptors
        for(ClassMapping classMapping : holder.mappings.values()) {
            for(MappingEntry methodEntry : classMapping.methodMappings.values()) {
                methodEntry.generateDescriptor();
            }
            for(MappingEntry fieldEntry : classMapping.fieldMappings.values()) {
                fieldEntry.generateDescriptor();
            }
        }
        return true;
    }

    public static boolean hasMappings() {
        return holder != null;
    }

    public static ClassMapping nms(String name) {
        return holder.clazz(name);
    }

    public static ClassMapping lastNms() {
        Validate.notNull(lastClass, "Tried to get last class, but was null");
        return lastClass;
    }

    public static MappingEntry lastNmsMethod() {
        Validate.notNull(lastClass, "Tried to get last method, but was null");
        return lastMethod;
    }

    public static MappingEntry lastNmsField() {
        Validate.notNull(lastClass, "Tried to get last field, but was null");
        return lastField;
    }

    public static final class MappingsHolder {
        private final Map<String, ClassMapping> mappings;

        private MappingsHolder() {
            this.mappings = new HashMap<>();
        }

        private MappingsHolder add(String name, ClassMapping mappings) {
            this.mappings.put(name, mappings);
            return this;
        }

        public ClassMapping clazz(String name) {
            Validate.isTrue(mappings.containsKey(name),
                            String.format("Tried to get invalid class mapping \"%s\"", name));
            return (lastClass = mappings.get(name));
        }
    }

    public static final class ClassMapping {
        private final String qualifiedName;
        private final String internalName;
        private final String descriptorName;
        private final Map<String, MappingEntry> methodMappings;
        private final Map<String, MappingEntry> fieldMappings;

        private ClassMapping(String qualifiedName) {
            this.qualifiedName = qualifiedName;
            this.internalName = qualifiedName.replace('.', '/');
            this.descriptorName = "L" + internalName + ";";
            this.methodMappings = new HashMap<>();
            this.fieldMappings = new HashMap<>();
        }

        private ClassMapping method(String name, MappingEntry entry) {
            entry.owner(this);
            entry.referenceName(name);
            methodMappings.put(name, entry);
            return this;
        }

        private ClassMapping field(String name, MappingEntry entry) {
            entry.owner(this);
            entry.referenceName(name);
            fieldMappings.put(name, entry);
            return this;
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

        public MappingEntry method(String name) {
            Validate.isTrue(methodMappings.containsKey(name),
                            String.format("Tried to get invalid method mapping \"%s\"", name));
            return (lastMethod = methodMappings.get(name));
        }

        public MappingEntry field(String name) {
            Validate.isTrue(fieldMappings.containsKey(name),
                            String.format("Tried to get invalid field mapping \"%s\"", name));
            return (lastField = fieldMappings.get(name));
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

    public static final class MappingEntry {
        private final String name;
        private String referenceName;
        private ClassMapping owner;
        private String descriptorFormat;
        private String descriptor;

        private MappingEntry(String value) {
            Validate.isTrue(value.contains(":") || value.contains("("),
                            "Mapping doesn't have descriptor, \"%s\"", value);
            value = value.replaceFirst("\\(", ":("); // Add a separator between method name and descriptor
            this.name = value.substring(0, value.indexOf(':'));
            this.descriptor(value.substring(value.indexOf(':') + 1));
        }

        private MappingEntry descriptor(String descriptor) {
            this.descriptorFormat = descriptor;
            return this;
        }

        private void owner(ClassMapping owner) {
            this.owner = owner;
        }

        private void referenceName(String referenceName) {
            this.referenceName = referenceName;
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
}