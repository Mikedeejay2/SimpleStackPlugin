package com.mikedeejay2.simplestack.mappings;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
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
import com.mikedeejay2.simplestack.bytecode.TypeConverter;
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

    public static boolean loadMappings(SimpleStack plugin) {
        try {
            return doLoadMappings(plugin, MinecraftVersion.VERSION);
        } catch(Throwable throwable) {
            SimpleStack.doCrash("Exception while generating NMS mappings", throwable, c -> {});
            return false;
        }
    }

    public static boolean validateMappings(SimpleStack plugin) {
        final Collection<ClassMapping> mappings = holder.get();
        final Map<ClassMapping, Exception> failedClasses = new HashMap<>();
        final Map<MappingEntry, Exception> failedEntries = new HashMap<>();
        // Class validation
        for(ClassMapping classMapping : mappings) {
            try {
                tryClassValidate(classMapping);
            } catch(ClassNotFoundException exception) {
                if(!tryAlternateMappings(classMapping)) {
                    failedClasses.put(classMapping, exception);
                }
            }
        }
        // Entry validation
        for(ClassMapping classMapping : mappings) {
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
        final boolean newMapping = !holder.contains(classRefName);
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
            if(holder.contains(classRefName) && !classObject.has("class_name")) {
                return Collections.emptyList();
            }
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
        for(MappingEntry entry : mapping.fieldEntries()) {
            try {
                tryValidateField(entry, clazz);
            } catch(Exception exception) {
                if(tryAlternateMappings(entry, clazz, false)) continue;
                failedEntries.put(entry, exception);
            }
        }
        for(MappingEntry entry : mapping.methodEntries()) {
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

    static ClassMapping setLast(ClassMapping classMapping) {
        lastClass = classMapping;
        return classMapping;
    }

    private static List<String> jsonArrayToStringList(JsonArray array) {
        return Lists.newArrayList(array).stream()
            .map(JsonElement::getAsString)
            .collect(Collectors.toList());
    }
}
