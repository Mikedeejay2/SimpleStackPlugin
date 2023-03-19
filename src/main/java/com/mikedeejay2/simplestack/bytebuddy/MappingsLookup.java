package com.mikedeejay2.simplestack.bytebuddy;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mikedeejay2.mikedeejay2lib.data.json.JsonAccessor;
import com.mikedeejay2.mikedeejay2lib.data.json.JsonFile;
import com.mikedeejay2.mikedeejay2lib.util.debug.CrashReport;
import com.mikedeejay2.mikedeejay2lib.util.debug.CrashReportSection;
import com.mikedeejay2.mikedeejay2lib.util.version.MinecraftVersion;
import com.mikedeejay2.simplestack.SimpleStack;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class MappingsLookup {
    private static MappingsHolder holder = null;
    private static ClassMapping lastClass = null;
    private static MappingEntry lastMethod = null;
    private static MappingEntry lastField = null;

    public static boolean loadMappings(SimpleStack plugin) {
        try {
            return doLoadMappings(plugin, MinecraftVersion.getVersionString());
        } catch(Throwable throwable) {
            CrashReport crashReport = new CrashReport(plugin, "Exception while generating NMS mappings", true, true);
            crashReport.setThrowable(throwable);

            plugin.fillCrashReport(crashReport);

            crashReport.addInfo(SimpleStack.CRASH_INFO_1)
                .addInfo(SimpleStack.CRASH_INFO_2)
                .addInfo(SimpleStack.CRASH_INFO_3);

            crashReport.execute();
            return false;
        }
    }

    public static boolean validateMappings(SimpleStack plugin) {
        final Map<String, ClassMapping> mappings = holder.mappings;
        final Map<ClassMapping, Exception> failedClasses = new HashMap<>();
        final Map<MappingEntry, Exception> failedEntries = new HashMap<>();
        for(ClassMapping classMapping : mappings.values()) {
            try {
                failedEntries.putAll(tryMappingValidate(classMapping));
            } catch(ClassNotFoundException exception) {
                failedClasses.put(classMapping, exception);
            }
        }

        if(!failedClasses.isEmpty() || !failedEntries.isEmpty()) {
            CrashReport crashReport = new CrashReport(plugin, "Exception while validating NMS mappings", true, true);

            CrashReportSection section = crashReport.addSection("Mapping Validation");
            section.addDetail("Failed Classes", getFailedClassesStr(failedClasses));
            section.addDetail("Failed Entries", getFailedEntriesStr(failedEntries));


            plugin.fillCrashReport(crashReport);

            crashReport.addInfo(SimpleStack.CRASH_INFO_1)
                .addInfo(SimpleStack.CRASH_INFO_2)
                .addInfo(SimpleStack.CRASH_INFO_3);

            crashReport.execute();
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
            Validate.isTrue(doLoadMappings(plugin, usingBaseElement.getAsString()),
                            "Unable to load base version \"%s\" for mappings version \"%s\"",
                            usingBaseElement.getAsString(), mcVersion);
        }

        // Iterate through classes in json
        for(String classKey : accessor.getKeys(false)) {
            final JsonElement classElement = accessor.get(classKey);
            if(classKey.equals("using_base")) continue;
            if(classElement.isJsonPrimitive()) { // Class entry holds no methods or fields, add it and continue
                // If class already exists, change name and continue
                if(holder.mappings.containsKey(classKey)) {
                    ClassMapping existingClassMapping = holder.mappings.get(classKey);
                    ClassMapping classMapping = new ClassMapping(classElement.getAsString());
                    classMapping.fieldMappings.putAll(existingClassMapping.fieldMappings);
                    classMapping.methodMappings.putAll(existingClassMapping.methodMappings);
                    holder.add(classKey, classMapping);
                    continue;
                }
                holder.add(classKey, new ClassMapping(classElement.getAsString()));
                continue;
            }
            Validate.isTrue(classElement.isJsonObject()); // Should be an object
            final JsonObject classObject = classElement.getAsJsonObject();

            // Create/get class mapping
            ClassMapping classMapping;
            if(!holder.mappings.containsKey(classKey)) { // If class NOT already loaded from a base
                Validate.isTrue(classObject.has("class_name"),
                                "Can not find class name for mapping \"%s\"", classKey); // Should have a class name
                classMapping = new ClassMapping(classObject.get("class_name").getAsString());
                holder.add(classKey, classMapping);
            } else {
                classMapping = holder.clazz(classKey);
                // If class already exists, change name and continue
                if(classObject.has("class_name")) {
                    ClassMapping tempClassMapping = new ClassMapping(classObject.get("class_name").getAsString());
                    tempClassMapping.fieldMappings.putAll(classMapping.fieldMappings);
                    tempClassMapping.methodMappings.putAll(classMapping.methodMappings);
                    classMapping = tempClassMapping;
                    holder.add(classKey, classMapping);
                }
            }

            // Collect methods and fields
            for(Map.Entry<String, MappingEntry> entry : collectEntries(classObject, "methods").entrySet()) {
                if(entry.getValue() == null) {
                    classMapping.methodMappings.remove(entry.getKey());
                    continue;
                }
                classMapping.method(entry.getKey(), entry.getValue());
            }
            for(Map.Entry<String, MappingEntry> entry : collectEntries(classObject, "fields").entrySet()) {
                if(entry.getValue() == null) {
                    classMapping.methodMappings.remove(entry.getKey());
                    continue;
                }
                classMapping.field(entry.getKey(), entry.getValue());
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

    private static Map<String, MappingEntry> collectEntries(JsonObject json, String key) {
        if(!json.has(key)) return ImmutableMap.of(); // Unable to collect if key doesn't exist
        final Map<String, MappingEntry> result = new HashMap<>();
        final JsonElement keyElement = json.get(key);
        Validate.isTrue(keyElement.isJsonObject()); // Should be an object
        final JsonObject keyObject = keyElement.getAsJsonObject();
        for(String curKey : keyObject.keySet()) {
            final JsonElement curElement = keyObject.get(curKey);
            Validate.isTrue(curElement.isJsonPrimitive()); // Should only be a String
            if(curElement.getAsString().equals("remove")) { // If remove, mark it null
                result.put(curKey, null);
                continue;
            }
            result.put(curKey, new MappingEntry(curElement.getAsString()));
        }
        return result;
    }

    private static String getFailedClassesStr(Map<ClassMapping, Exception> failed) {
        if(failed.isEmpty()) return "None";
        StringBuilder builder = new StringBuilder();
        for(Map.Entry<ClassMapping, Exception> entry : failed.entrySet()) {
            final ClassMapping mapping = entry.getKey();
            final Exception exception = entry.getValue();
            builder.append("\n    ")
                .append(mapping.qualifiedName())
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
                .append(".")
                .append(mapping.name())
                .append(":")
                .append(mapping.descriptor())
                .append(", exception: ")
                .append(exception.getClass().getSimpleName())
                .append(" ")
                .append(exception.getMessage());
        }
        return builder.toString();
    }

    private static Map<MappingEntry, Exception> tryMappingValidate(ClassMapping mapping)
        throws ClassNotFoundException {
        Class<?> clazz = Class.forName(mapping.qualifiedName(), true, Bukkit.class.getClassLoader());
        Map<MappingEntry, Exception> failedEntries = new HashMap<>();
        for(MappingEntry entry : mapping.fieldMappings.values()) {
            try {
                tryValidateField(entry, clazz);
            } catch(Exception exception) {
                if(trySuperClasses(entry, clazz, false)) continue;
                failedEntries.put(entry, exception);
            }
        }
        for(MappingEntry entry : mapping.methodMappings.values()) {
            try {
                tryValidateMethod(entry, clazz);
            } catch(Exception exception) {
                if(trySuperClasses(entry, clazz, true)) continue;
                failedEntries.put(entry, exception);
            }
        }
        return failedEntries;
    }

    private static boolean trySuperClasses(MappingEntry mapping, Class<?> clazz, boolean method) {
        Class<?> currentClass = clazz.getSuperclass();
        while(currentClass != null) {
            try {
                if (method) tryValidateMethod(mapping, currentClass);
                else tryValidateField(mapping, currentClass);
                return true;
            } catch(Exception ignored) {
                // ignored
            }
            currentClass = currentClass.getSuperclass();
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
        if(!method.getReturnType().equals(returnType)) {
            throw new NoSuchMethodException(String.format(
                "Mismatch return type for method \"%s\", expected type \"%s\", actual type \"%s\"",
                method.getName(), returnType.getName(), method.getReturnType()));
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
        if(!field.getType().equals(typeClass)) {
            throw new NoSuchFieldException(String.format(
                "Mismatch type for field \"%s\", expected type \"%s\", actual type \"%s\"",
                field.getName(), typeClass.getName(), field.getType()));
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
