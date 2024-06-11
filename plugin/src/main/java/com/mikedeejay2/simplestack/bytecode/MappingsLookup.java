package com.mikedeejay2.simplestack.bytecode;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mikedeejay2.mikedeejay2lib.data.json.JsonAccessor;
import com.mikedeejay2.mikedeejay2lib.data.json.JsonFile;
import com.mikedeejay2.mikedeejay2lib.util.debug.CrashReportSection;
import com.mikedeejay2.mikedeejay2lib.util.structure.tuple.ImmutablePair;
import com.mikedeejay2.mikedeejay2lib.util.structure.tuple.Pair;
import com.mikedeejay2.mikedeejay2lib.util.version.MinecraftVersion;
import com.mikedeejay2.simplestack.SimpleStack;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class MappingsLookup {
    private static MappingsHolder holder = null;
    private static ClassMapping lastClass = null;
    private static MappingEntry lastMethod = null;
    private static MappingEntry lastField = null;

    public static boolean loadMappings(SimpleStack plugin) {
        try {
            return doLoadMappings(plugin, MinecraftVersion.VERSION);
        } catch(Throwable throwable) {
            SimpleStack.doCrash("Exception while generating NMS mappings", throwable, c -> {});
            return false;
        }
    }

    public static boolean validateMappings(SimpleStack plugin) {
        final Map<String, ClassMapping> mappings = holder.mappings;
        final Map<ClassMapping, Exception> failedClasses = new HashMap<>();
        final Map<MappingEntry, Exception> failedEntries = new HashMap<>();
        // Class validation
        for(ClassMapping classMapping : mappings.values()) {
            try {
                tryClassValidate(classMapping);
            } catch(ClassNotFoundException exception) {
                if(!tryAlternateMappings(classMapping)) {
                    failedClasses.put(classMapping, exception);
                }
            }
        }
        // Entry validation
        for(ClassMapping classMapping : mappings.values()) {
            try {
                failedEntries.putAll(tryMappingValidate(classMapping));
            } catch(ClassNotFoundException exception) {
                failedClasses.put(classMapping, exception);
            }
        }

        if(!failedClasses.isEmpty() || !failedEntries.isEmpty()) {
            SimpleStack.doCrash("Exception while validating NMS mappings", null, crashReport -> {
                CrashReportSection section = crashReport.addSection("Mapping Validation");
                section.addDetail("Failed Classes", getFailedClassesStr(failedClasses));
                section.addDetail("Failed Entries", getFailedEntriesStr(failedEntries));
            });
            return false;
        }
        return true;
    }

    private static boolean doLoadMappings(SimpleStack plugin, String mcVersion) {
        final JsonFile jsonFile = new JsonFile(plugin, String.format("nms/%s.json", mcVersion));
        if(!jsonFile.loadFromJar(false)) return false;
        final JsonAccessor accessor = jsonFile.getAccessor();
        if(holder == null) holder = new MappingsHolder();
        // Special case, load base json mappings
        if(accessor.contains("using_base")) {
            JsonElement usingBaseElement = accessor.get("using_base");
            Validate.isTrue(
                doLoadMappings(plugin, usingBaseElement.getAsString()),
                "Unable to load base version \"%s\" for mappings version \"%s\"",
                usingBaseElement.getAsString(), mcVersion);
        }

        // Iterate through classes in json
        for(String classRefName : accessor.getKeys(false)) {
            final JsonElement classElement = accessor.get(classRefName);
            if(classRefName.equals("using_base")) continue;
            collectClass(classRefName, classElement);
        }
        return true;
    }

    private static void collectClass(String classRefName, JsonElement classElement) {
        final List<String> allNames = collectClassNames(classElement, classRefName);
        final String qualifiedName = !allNames.isEmpty() ? allNames.remove(0) : null;
        final boolean newMapping = !holder.mappings.containsKey(classRefName);
        Validate.isTrue(!(newMapping && qualifiedName == null),
                        "New class mapping with no qualified name: \"%s\"", classRefName);
        final ClassMapping mapping = newMapping ? new ClassMapping(qualifiedName, classRefName) : holder.clazz(classRefName);

        // If it's not a new mapping and there's a new qualified name, change the existing mapping
        if(!newMapping && qualifiedName != null) mapping.setQualifiedName(qualifiedName);
        // If it's a new mapping, register it
        if(newMapping) holder.add(classRefName, mapping);
        // If it's a primitive, no more work needs to be done
        if(classElement.isJsonPrimitive()) return;

        // Add alternates
        mapping.alternates(allNames);
        // If it's an array, no more work needs to be done
        if(classElement.isJsonArray()) return;

        Validate.isTrue(classElement.isJsonObject()); // Should be an object
        final JsonObject classObject = classElement.getAsJsonObject();

        // Collect methods and fields
        for(Map.Entry<String, MappingEntry> entry : collectEntries(classObject, "methods").entrySet()) {
            if(entry.getValue() == null) {
                mapping.removeMethod(entry.getKey());
                continue;
            }
            mapping.method(entry.getKey(), entry.getValue());
        }
        for(Map.Entry<String, MappingEntry> entry : collectEntries(classObject, "fields").entrySet()) {
            if(entry.getValue() == null) {
                mapping.removeField(entry.getKey());
                continue;
            }
            mapping.field(entry.getKey(), entry.getValue());
        }
    }

    private static List<String> collectClassNames(JsonElement classElement, String classRefName) {
        return collectClassNames(classElement, classRefName, false);
    }

    private static List<String> collectClassNames(JsonElement classElement, String classRefName, boolean inner) {
        if(classElement.isJsonObject() && !inner) {
            final JsonObject classObject = classElement.getAsJsonObject();
            Validate.isTrue(classObject.has("class_name"),
                            "Can not find class name for mapping \"%s\"", classRefName); // Should have a class name
            return collectClassNames(classObject.get("class_name"), classRefName, true);
        }
        if(classElement.isJsonArray()) {
            final JsonArray array = classElement.getAsJsonArray();
            Validate.isTrue(!array.isEmpty(),
                            "Can not find a class name for \"%s\"", classRefName); // Should have a value
            return jsonArrayToStringList(array);
        }
        if(classElement.isJsonPrimitive()) {
            return Lists.newArrayList(classElement.getAsString());
        }
        throw new UnsupportedOperationException("Unknown class name data type for class " + classRefName);
    }

    private static Map<String, MappingEntry> collectEntries(JsonObject json, String key) {
        if(!json.has(key)) return Collections.emptyMap(); // Unable to collect if key doesn't exist
        final Map<String, MappingEntry> result = new HashMap<>();
        final JsonElement keyElement = json.get(key);
        Validate.isTrue(keyElement.isJsonObject()); // Should be an object
        final JsonObject keyObject = keyElement.getAsJsonObject();

        for(String entryKey : keyObject.keySet()) {
            final JsonElement entryElement = keyObject.get(entryKey);
            final Pair<String, MappingEntry> resultPair = collectEntryValue(entryKey, entryElement);
            result.put(resultPair.getKey(), resultPair.getValue());
        }
        return result;
    }

    private static Pair<String, MappingEntry> collectEntryValue(String entryKey, JsonElement entryElement) {
        if(entryElement.isJsonPrimitive()) {
            if(entryElement.getAsString().equals("remove")) { // If remove, mark it null
                return new ImmutablePair<>(entryKey, null);
            }
            return new ImmutablePair<>(entryKey, new MappingEntry(entryElement.getAsString(), entryKey));
        }

        Validate.isTrue(entryElement.isJsonArray()); // Should be an array
        final JsonArray array = entryElement.getAsJsonArray();

        Validate.isTrue(!array.isEmpty(),
                        "Can not find a value for entry \"%s\"", entryKey); // Should have a value
        List<String> allValues = jsonArrayToStringList(entryElement.getAsJsonArray());
        final MappingEntry mappingEntry = new MappingEntry(allValues.remove(0), entryKey);

        // Collect alternates
        mappingEntry.alternates(allValues);
        return new ImmutablePair<>(entryKey, mappingEntry);
    }

    private static String getFailedClassesStr(Map<ClassMapping, Exception> failed) {
        if(failed.isEmpty()) return "None";
        StringBuilder builder = new StringBuilder();
        for(Map.Entry<ClassMapping, Exception> entry : failed.entrySet()) {
            final ClassMapping mapping = entry.getKey();
            final Exception exception = entry.getValue();
            builder.append("\n    ")
                .append(mapping.referenceName())
                .append(", exception: ")
                .append(exception.getClass().getSimpleName())
                .append(" ")
                .append(exception.getMessage());
        }
        return builder.toString();
    }

    private static String getFailedEntriesStr(Map<MappingEntry, Exception> failed) {
        if(failed.isEmpty()) return "None";
        StringBuilder builder = new StringBuilder();
        for(Map.Entry<MappingEntry, Exception> entry : failed.entrySet()) {
            final MappingEntry mapping = entry.getKey();
            final Exception exception = entry.getValue();
            builder.append("\n    ")
                .append(mapping.owner().qualifiedName())
                .append("::")
                .append(mapping.referenceName())
                .append(", exception: ")
                .append(exception.getClass().getSimpleName())
                .append(" ")
                .append(exception.getMessage());
        }
        return builder.toString();
    }

    private static void tryClassValidate(ClassMapping mapping)
        throws ClassNotFoundException {
        // Exception thrown if class not found
        Class<?> clazz = Class.forName(mapping.qualifiedName(), true, Bukkit.class.getClassLoader());
    }

    private static boolean tryAlternateMappings(ClassMapping parentMapping) {
        for(String alternate : parentMapping.alternates()) {
            try {
                ClassMapping alternateMapping = new ClassMapping(alternate, parentMapping.referenceName());
                tryClassValidate(alternateMapping);
                parentMapping.useAlternate(alternate);
                return true;
            } catch(Exception exception) {
                // ignored
            }
        }
        return false;
    }

    private static Map<MappingEntry, Exception> tryMappingValidate(ClassMapping mapping)
        throws ClassNotFoundException {
        final Class<?> clazz = Class.forName(mapping.qualifiedName(), true, Bukkit.class.getClassLoader());
        Map<MappingEntry, Exception> failedEntries = new HashMap<>();
        for(MappingEntry entry : mapping.fieldMappings.values()) {
            try {
                tryValidateField(entry, clazz);
            } catch(Exception exception) {
                if(tryAlternateMappings(entry, clazz, false)) continue;
                failedEntries.put(entry, exception);
            }
        }
        for(MappingEntry entry : mapping.methodMappings.values()) {
            try {
                tryValidateMethod(entry, clazz);
            } catch(Exception exception) {
                if(tryAlternateMappings(entry, clazz, true)) continue;
                failedEntries.put(entry, exception);
            }
        }
        return failedEntries;
    }

    private static boolean tryAlternateMappings(MappingEntry parentMapping, Class<?> clazz, boolean method) {
        for(String alternate : parentMapping.alternates()) {
            try {
                final MappingEntry alternateEntry = new MappingEntry(alternate, parentMapping.referenceName());
                if (method) tryValidateMethod(alternateEntry, clazz);
                else tryValidateField(alternateEntry, clazz);
                parentMapping.useAlternate(alternate);
                return true;
            } catch(Exception exception) {
                // ignored
            }
        }
        return false;
    }

    private static void tryValidateMethod(MappingEntry mapping, Class<?> clazz)
        throws ClassNotFoundException, NoSuchMethodException {
        if(mapping.name().equals("<init>")) {
            tryValidateConstructor(mapping, clazz);
            return;
        }
        final String descriptor = mapping.descriptor();
        final List<Class<?>> paramTypes = TypeConverter.convertParameterTypes(descriptor);
        final Class<?> returnType = TypeConverter.convertReturnType(descriptor);

        Method method = clazz.getDeclaredMethod(mapping.name(), paramTypes.toArray(new Class<?>[0]));
        if(!mapping.name().equals(method.getName())) {
            throw new NoSuchMethodException(String.format(
                "Mismatch name for method \"%s\", expected name \"%s\", actual name \"%s\"",
                mapping.referenceName(), mapping.name(), method.getName()
            ));
        }

        if(!method.getReturnType().equals(returnType)) {
            throw new NoSuchMethodException(String.format(
                "Mismatch return type for method \"%s\", expected type \"%s\", actual type \"%s\"",
                mapping.name(), returnType.getSimpleName(), method.getReturnType().getSimpleName()));
        }
    }

    private static void tryValidateConstructor(MappingEntry mapping, Class<?> clazz)
        throws ClassNotFoundException, NoSuchMethodException {
        final String descriptor = mapping.descriptor();
        final List<Class<?>> paramTypes = TypeConverter.convertParameterTypes(descriptor);

        // Exception thrown if constructor not found
        Constructor<?> constructor = clazz.getDeclaredConstructor(paramTypes.toArray(new Class<?>[0]));
    }

    private static void tryValidateField(MappingEntry mapping, Class<?> clazz)
        throws ClassNotFoundException, NoSuchFieldException {
        final String descriptor = mapping.descriptor();
        final Class<?> typeClass = TypeConverter.convertType(descriptor);

        Field field = clazz.getDeclaredField(mapping.name());
        if(!mapping.name().equals(field.getName())) {
            throw new NoSuchFieldException(String.format(
                "Mismatch name for field \"%s\", expected name \"%s\", actual name \"%s\"",
                mapping.referenceName(), mapping.name(), field.getName()
            ));
        }

        if(!field.getType().equals(typeClass)) {
            throw new NoSuchFieldException(String.format(
                "Mismatch type for field \"%s\", expected type \"%s\", actual type \"%s\"",
                mapping.referenceName(), typeClass.getSimpleName(), field.getType().getSimpleName()));
        }
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

    private static List<String> jsonArrayToStringList(JsonArray array) {
        return array.asList().stream()
            .map(JsonElement::getAsString)
            .collect(Collectors.toList());
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
        private final String referenceName;
        private String qualifiedName;
        private String internalName;
        private String descriptorName;
        private final Map<String, MappingEntry> methodMappings;
        private final Map<String, MappingEntry> fieldMappings;
        private final List<String> alternates;

        private ClassMapping(String qualifiedName, String referenceName) {
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

        private ClassMapping method(String name, MappingEntry entry) {
            entry.owner(this);
            methodMappings.put(name, entry);
            return this;
        }

        private ClassMapping removeMethod(String name) {
            methodMappings.remove(name);
            return this;
        }

        private ClassMapping field(String name, MappingEntry entry) {
            entry.owner(this);
            fieldMappings.put(name, entry);
            return this;
        }

        private ClassMapping removeField(String name) {
            fieldMappings.remove(name);
            return this;
        }

        private ClassMapping setQualifiedName(String qualifiedName) {
            initFields(qualifiedName);
            return this;
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

        private void alternate(String alternate) {
            this.alternates.add(alternate);
        }

        private void alternates(List<String> alternate) {
            this.alternates.addAll(alternate);
        }

        public List<String> alternates() {
            return alternates;
        }

        private void useAlternate(String alternate) {
            Validate.isTrue(alternates.contains(alternate), "Tried to use mapping that wasn't an alternate");
            initFields(alternate);
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
        private final String referenceName;
        private String name;
        private ClassMapping owner;
        private String descriptorFormat;
        private String descriptor;
        private final List<String> alternates;

        private MappingEntry(String value, String referenceName) {
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

        private void descriptor(String descriptor) {
            this.descriptorFormat = descriptor;
            this.descriptor = null;
        }

        private void owner(ClassMapping owner) {
            this.owner = owner;
        }

        private void alternate(String alternate) {
            this.alternates.add(alternate);
        }

        private void alternates(List<String> alternate) {
            this.alternates.addAll(alternate);
        }

        public List<String> alternates() {
            return alternates;
        }

        private void useAlternate(String alternate) {
            Validate.isTrue(alternates.contains(alternate), "Tried to use mapping that wasn't an alternate");
            initFields(alternate);
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
}
